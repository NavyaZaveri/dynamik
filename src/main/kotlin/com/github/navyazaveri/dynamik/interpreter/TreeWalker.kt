package com.github.navyazaveri.dynamik.interpreter

import com.github.navyazaveri.dynamik.errors.UnexpectedType
import com.github.NavyaZaveri.dynamik.expressions.*
import com.github.navyazaveri.dynamik.expressions.*
import expressions.*
import interpreter.Environment
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import com.github.navyazaveri.dynamik.scanner.TokenType

class TreeWalker : ExpressionVisitor<Any>,
    StatementVisitor<Any> {
    var env = Environment()

    /**
    Waits until all par functions invoked thus far have finished
    running.
     */
    override fun visitWaitStmt(waitStmt: WaitStmt): Any {
        return runBlocking { }
    }

    override fun visitParStatement(parStmt: ParStmt): Any {
        GlobalScope.launch {
            this@TreeWalker.visitCallExpression(parStmt.callExpr, true)
        }
        return Any()
    }


    override fun visitReturnStatement(returnStmt: ReturnStmt): Any {
        val result = evaluate(returnStmt.statement)
        throw Return(result)
    }

    override fun visitIfStmt(ifStmt: IfStmt) {
        val condition = evaluate(ifStmt.condition)
        if (isType<Boolean>(condition, throwException = true) && condition as Boolean) {
            ifStmt.body.forEach { evaluate(it) }
        }
    }

    /* Calls a function, with arguments passed by value.
     */
    override fun visitCallExpression(callExpr: CallExpr, par: Boolean): Any {
        val callable = env.get(callExpr.funcName) as Callable
        val args = callExpr.args.map { it.evaluateBy(this) }
        val mainEnv = env


        /** If the function needs to spawned in parallel, simply invoke the
        function against a *new* interpreter instance. So each par function has
        its own fresh environment, ensuring the environment belonging to the
        main sequential thread can never be overwritten/reassigned.
         */
        if (par) {
            val newInterpreter = TreeWalker()
            this.env.globals()
                .forEach { (k, v) -> newInterpreter.env.define(k, v.value, VariableStatus.VAL) }

            return callable.invoke(args, newInterpreter)
        }

        /** Invokes the function with a new environment. .
         * The original environment is preserved  and assigned to `env` when the function ends.
         */
        return callable.invoke(args, this).also { this.env = mainEnv }
    }

    override fun visitFnStatement(fnStmt: FnStmt) {
        when (fnStmt.memoize) {
            true -> env.define(fnStmt.functionName.lexeme,
                MemoizedCallable(fnStmt), VariableStatus.VAL)
            false -> env.define(fnStmt.functionName.lexeme,
                DynamikCallable(fnStmt), VariableStatus.VAL)
        }
    }

    override fun visitWhileStatement(whileStmt: WhileStmt) {
        var condition = evaluate(whileStmt.expr)
        while (isType<Boolean>(condition, throwException = true) && (condition as Boolean)) {
            whileStmt.stmts.forEach { evaluate(it) }
            condition = evaluate(whileStmt.expr)
        }
    }

    inline fun <reified T> isType(vararg objects: Any, throwException: Boolean = false): Boolean {
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

    override fun visitVariableExpr(variableExpr: VariableExpr): Any = env.get(variableExpr.token.lexeme)

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
        println("${evaluate(printStmt.expr)}")
    }

    fun evaluate(expr: Expr): Any = expr.evaluateBy(this)

    fun evaluate(stmt: Stmt): Any = stmt.evaluateBy(this)

    private fun concatOrAdd(left: Any, right: Any): Any {
        if (isType<String>(left, right)) {
            return left as String + right as String
        }
        if (isType<Double>(left, right)) {
            return left as Double + right as Double
        }
        throw UnexpectedType(
            "${Pair(
                left,
                right
            )}} need to be either Doubles or Strings"
        )
    }

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
            TokenType.AND -> {
                if (isType<Boolean>(left, right, throwException = true))
                    return left as Boolean && right as Boolean
            }
            TokenType.AND_AND -> {
                isType<Boolean>(left, right, throwException = true)
                return left as Boolean && right as Boolean
            }
            TokenType.BANG_EQUAL -> return left != right
            TokenType.LESS -> return (left as Double) < (right as Double)
            TokenType.LESS_EQUAL -> return (left as Double) <= (right as Double)
            TokenType.GREATER -> return (left as Double) > (right as Double)
            TokenType.GREATER_EQUAL -> return (left as Double) >= (right as Double)
        }

        throw UnexpectedType("${expr.operand.type}  not recognized")
    }

    override fun visitUnaryExpression(expr: UnaryExpr): Any {
        val l = evaluate(expr.left)
        when (expr.token.type) {
            TokenType.MINUS -> return -(l as Double)
            TokenType.BANG -> return !(l as Boolean)
        }
        throw UnexpectedType("could not evaluate ${expr.token.type} for unary $l")
    }

    override fun visitLiteralExpression(expr: LiteralExpr): Any = expr.token.literal
}

fun List<Stmt>.evaluateAllBy(evaluator: StatementVisitor<*>): Any {
    return this.forEach { it.evaluateBy(evaluator) }
}