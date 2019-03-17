package interpreter

import expressions.*
import parser.StmtParser
import parser.parseStmts
import scanner.Scanner
import scanner.TokenType
import scanner.tokenize


class TreeWalker : ExpressionVisitor<Any>, StatementVisitor<Any> {
    val env = Environment()
    override fun visitWhileStatement(whileStmt: WhileStmt): Any {
        var condition = evaluate(whileStmt.expr)
        while (isType<Boolean>(condition) && condition as Boolean) {
            whileStmt.stmts.forEach { evaluate(it) }
            condition = evaluate(whileStmt.expr)
        }
        return Any()
    }

    inline fun <reified T> isType(vararg objects: Any, throwException: Boolean = false): Boolean {
        val allTypesMatch = objects.all { it is T }
        if (allTypesMatch) {
            return true
        }
        if (throwException) {
            throw RuntimeException()
        }
        return false
    }

    fun evaluateStmts(stmts: List<Stmt>): Unit = stmts.forEach { evaluate(it) }

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
        return left as Double + right as Double
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
        throw RuntimeException("${expr.operand.type}  not recognized")
    }

    override fun visitUnaryExpression(expr: UnaryExpr): Any {
        val l = evaluate(expr.left)
        when (expr.token.type) {
            TokenType.MINUS -> return -(l as Double)
            TokenType.BANG -> return !(l as Boolean)
        }
        throw RuntimeException("could not evaluateAllBy ${expr.token.type} for unary $l")
    }

    override fun visitLiteralExpression(expr: LiteralExpr): Any = expr.token.literal
}

fun main(args: Array<String>) {
    val toks = Scanner().tokenize("var d=4; d=d+1; val x =2; print (d+x); var s = \"hello\"; print s+\" world\"; ")
    val ast = StmtParser(toks).parseStmts()
    TreeWalker().evaluateStmts(ast)
    "var d=4; print 110;".tokenize().parseStmts().evaluateAllBy(TreeWalker())
}

fun <T> List<Stmt>.evaluateAllBy(evaluator: StatementVisitor<T>): Any {
    return this.forEach { it.evaluateBy(evaluator) }
}




