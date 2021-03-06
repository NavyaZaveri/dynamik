package com.github.navyazaveri.dynamik.interpreter

import com.github.navyazaveri.dynamik.errors.AssertionErr
import com.github.navyazaveri.dynamik.errors.UnexpectedType
import com.github.navyazaveri.dynamik.errors.VariableNotInScope
import com.github.navyazaveri.dynamik.expressions.*
import com.github.navyazaveri.dynamik.scanner.TokenType
import com.github.navyazaveri.dynamik.stdlib.clockCallable
import com.github.navyazaveri.dynamik.stdlib.containers.DynamikList
import com.github.navyazaveri.dynamik.stdlib.containers.DynamikMap
import com.github.navyazaveri.dynamik.stdlib.containers.ListInstance
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock


/**
 * A tree-walking interpreter that visits AST nodes using methods defined in [ExpressionVisitor] and [StatementVisitor].
 */
class TreeWalker(var env: Environment = Environment()) : ExpressionVisitor<Any>, StatementVisitor<Any> {
    override fun visitConcatExpr(concatExpr: ConcatExpr): Any {
        val subLists = concatExpr.containers.map { evaluate(it) as ListInstance }
        return subLists.fold(ListInstance()) { acc, list -> acc.concat(list) }
    }


    override fun visitChainedStmt(chainedStmt: ChainedStmt): Any {
        evaluate(chainedStmt.x)
        return evaluate(chainedStmt.y)
    }

    override fun visitAssignExpr(assignExpr: AssignExpr): Any {
        env.assign(assignExpr.tok.lexeme, evaluate(assignExpr.expr))
        return Any()
    }

    override fun visitThisExpr(thisExpr: ThisExpr): Any {
        Environment.globalAccess = false
        return evaluate(thisExpr.expr).also { Environment.globalAccess = true }
    }

    override fun visitThisStmt(thisStmt: ThisStmt): Any {
        return evaluate(thisStmt.stmt)
    }

    val jobs = mutableListOf<Job>()

    init {
        env.defineClass("list", DynamikList(), global = true)
        env.defineClass("map", DynamikMap(), global = true)
        env.defineFunction("clock", clockCallable(), global = true)
    }

    override fun visitInstanceStmt(instanceStmt: InstanceStmt): Any {
        val instance = env.get(instanceStmt.name) as DynamikInstance
        //mutable borrows instance environment
        val oldEnv = this.env
        this.env = instance.env

        // we temporarily give the instance environment access to
        // the enclosing environment. This is in order to evaluate the
        // arguments passed to the method invocation.
        this.env.outer = oldEnv.identifierToValue.toMutableMap()
        return evaluate(instanceStmt.stmt).also { this.env = oldEnv; }
    }

    override fun visitClazzExpression(instanceExpr: InstanceExpr): Any {
        val instance = env.get(instanceExpr.clazzName) as DynamikInstance
        val oldEnv = this.env
        this.env = instance.env
        this.env.outer = oldEnv.identifierToValue.toMutableMap()
        return evaluate(instanceExpr.expr).also { this.env = oldEnv; }
    }

    override fun visitClassStmt(classStmt: ClassStmt): Any {

        env.defineClass(
            classStmt.name,
            DefaultClass(classStmt.name, classStmt.methods, classStmt.fields)
        )
        return Any()
    }


    fun clear() {
        env.clear()
    }

    override fun visitSkipStatement(skipStmt: SkipStmt): Any {
        return Any()
    }

    override fun visitAssertStmt(assertStmt: AssertStmt): Any {
        val assertion = evaluate(assertStmt.e1) as Boolean
        if (!assertion) {
            throw AssertionErr(assertion)
        }
        return true
    }

    override fun visitGlobalStmt(globalStmt: GlobalStmt): Any {
        val globalValue = evaluate(globalStmt.value)
        //      env.define(globalStmt.name.lexeme, globalValue, VariableStatus.VAL)
        Environment.addGlobal(globalStmt.name.lexeme, globalValue)
        return Any()
    }

    /**
    Waits until all par functions invoked thus far have finished
    running.
     */
    override fun visitWaitStmt(waitStmt: WaitStmt): Any {
        runBlocking {
            jobs.forEach { it.join() }
            jobs.clear()
        }

        return Any()
    }

    override fun visitParStatement(parStmt: ParStmt): Any {

        GlobalScope.launch {
            when (parStmt.lock) {
                true -> Mutex().withLock { this@TreeWalker.visitCallExpression(parStmt.callExpr, true) }
                false -> this@TreeWalker.visitCallExpression(parStmt.callExpr, true)
            }
        }
        return Any()
    }

    override fun visitReturnStatement(returnStmt: ReturnStmt): Any {
        val result = evaluate(returnStmt.statement)
        throw Return(result)
    }

    override fun visitIfStmt(ifStmt: IfStmt) {
        val condition = evaluate(ifStmt.condition) as Boolean
        if (condition) {
            ifStmt.body.forEach { evaluate(it) }
        } else {
            ifStmt.elseBody.forEach { evaluate(it) }
        }
    }

    override fun visitCallExpression(callExpr: CallExpr, par: Boolean): Any {
        val callable = env.getCallable(callExpr.funcName)
        val args: MutableList<Any> =
            callExpr.args.map { this.evaluate(it) }.toMutableList()

        val mainEnv = env


        /** If the function needs to spawned in parallel, simply invoke the
        function against a *new* interpreter instance. So each par function has
        its own fresh environment, ensuring the environment belonging to the
        main sequential thread can never be overwritten/reassigned.
         */
        if (par) {
            val newInterpreter = TreeWalker()
            this.env.functions()
                .forEach { (k, v) -> newInterpreter.env.defineFunction(k, v.value) }

            return callable.invoke(args, newInterpreter)
        }

        // Invokes the function with a new environment.
        // The original environment is preserved  and assigned to `env` when the function ends.

        return callable.invoke(args, this).also { this.env = mainEnv }
    }

    /**
     * Creates a function and puts it in [env].
     */
    override fun visitFnStatement(fnStmt: FnStmt) {
        when (fnStmt.memoize) {
            true ->
                env.defineFunction(
                    fnStmt.functionName.lexeme,
                    MemoizedFunction(fnStmt)
                )
            false ->
                env.defineFunction(
                    fnStmt.functionName.lexeme,
                    DefaultFunction(fnStmt)
                )
        }
    }

    override fun visitWhileStatement(whileStmt: WhileStmt) {
        var condition = evaluate(whileStmt.condition) as Boolean
        while (condition) {
            whileStmt.stmts.forEach { evaluate(it) }
            condition = evaluate(whileStmt.condition) as Boolean
        }
    }

    private inline fun <reified T> isType(vararg objects: Any, throwException: Boolean = false): Boolean {
        val allTypesMatch = objects.all { it is T }
        if (allTypesMatch) {
            return true
        }
        if (throwException) {
            throw UnexpectedType("expecting ${T::class.java}")
        }
        return false
    }

    fun evaluateStmts(stmts: List<Stmt>, env: Environment = this.env) {
        this.env = env
        stmts.forEach { evaluate(it) }
    }

    override fun visitVariableExpr(variableExpr: VariableExpr): Any {
        try {
            return env.get(variableExpr.token.lexeme)
        } catch (v: VariableNotInScope) { //if it doesn't exist in the current, look into outer scope.
            return env.outer[variableExpr.token.lexeme]?.value ?: throw VariableNotInScope(
                variableExpr.token.lexeme,
                env.identifierToValue.keys
            )
        }
    }

    override fun visitVariableStmt(varStmt: VarStmt) {
        env.define(varStmt.name.lexeme, evaluate(varStmt.expr), VariableStatus.VAR)
    }

    override fun visitValStmt(valStmt: ValStmt) {
        env.define(valStmt.name.lexeme, evaluate(valStmt.expr), VariableStatus.VAL)
    }

    override fun visitAssignStmt(assignStmt: AssignStmt) {
        env.assign(assignStmt.token.lexeme, evaluate(assignStmt.expr))
    }

    override fun visitExpressionsStatement(exprStmt: ExprStmt): Any = evaluate(exprStmt.expr)

    override fun visitPrintStmt(printStmt: PrintStmt) {
        println("${evaluate(printStmt.stmt)}")
    }

    fun evaluate(expr: Expr, env: Environment = this.env): Any {
        this.env = env
        return expr.evaluateBy(this)
    }

    fun evaluate(stmt: Stmt, env: Environment = this.env): Any {
        this.env = env
        return stmt.evaluateBy(this)
    }

    private fun concatOrAdd(left: Any, right: Any): Any {
        if (isType<String>(left, right)) {
            return left as String + right as String
        }
        if (isType<Double>(left, right)) {
            return left as Double + right as Double
        }
        throw UnexpectedType("${Pair(left, right)} need to be either Doubles or Strings")
    }

    @Throws(UnexpectedType::class)
    override
    fun visitBinaryExpression(expr: BinaryExpr): Any {
        val left = evaluate(expr.left)
        val right = evaluate(expr.right)

        when (expr.operand.type) {
            TokenType.PLUS -> return concatOrAdd(left, right)
            TokenType.MINUS -> return left as Double - right as Double
            TokenType.SLASH -> return left as Double / right as Double
            TokenType.STAR -> return left as Double * right as Double
            TokenType.EQUAL_EQUAL -> return left == right
            TokenType.AND_AND -> {
                if (left as Boolean && right as Boolean)
                    return left && right
            }

            TokenType.BANG_EQUAL -> return left != right
            TokenType.LESS -> return (left as Double) < (right as Double)
            TokenType.LESS_EQUAL -> return (left as Double) <= (right as Double)
            TokenType.GREATER -> return (left as Double) > (right as Double)
            TokenType.GREATER_EQUAL -> return (left as Double) >= (right as Double)
            TokenType.PLUS_PLUS -> {
                if (left is ListInstance && right is ListInstance) {
                    return left.concat(right)
                }
                throw UnexpectedType("cannot concat $left and $right. Can only concat lists")
            }
            else -> throw UnexpectedType("${expr.operand.type}  not recognized at line ${expr.operand.line}")
        }
        throw RuntimeException("unreachable")
    }


    override fun visitUnaryExpression(expr: UnaryExpr): Any {
        val result = evaluate(expr.left)
        return when (expr.token.type) {
            TokenType.MINUS -> -(result as Double)
            TokenType.BANG -> !(result as Boolean)
            else -> throw UnexpectedType("could not evaluate ${expr.token.type} for unary $result at line ${expr.token.line}")
        }
    }

    override fun visitLiteralExpression(expr: LiteralExpr): Any = expr.token.literal
}

fun <T> List<Stmt>.evaluateAllBy(visitor: StatementVisitor<T>) {
    this.forEach { it.evaluateBy(visitor) }
}


