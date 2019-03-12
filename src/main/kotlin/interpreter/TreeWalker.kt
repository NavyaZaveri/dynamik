package interpreter

import expressions.*
import parser.ExprParser
import scanner.Scanner
import scanner.TokenType


class TreeWalker : ExpressionVisitor<Any>, StatementVisitor<Unit> {
    val env = Environment()


    override fun visitVariableStmt(varStmt: VarStmt) {
        env.define(varStmt.name.lexeme, evaluate(varStmt.expr), VariableStatus.VAR)
    }

    override fun visitValStmt(valStmt: ValStmt) {
        env.define(valStmt.name.lexeme, evaluate(valStmt.expr), VariableStatus.VAL)
    }

    override fun visitAssignStmt(assignStmt: AssignStmt) {
        env.assign(assignStmt.token.lexeme, evaluate(assignStmt.expr))
    }

    override fun visitExpressionsStatement(exprStmt: ExprStmt) {
        evaluate(exprStmt.expr)
    }

    override fun visitPrintStmt(printStmt: PrintStmt) {
        print("print " + evaluate(printStmt.expr))
    }

    fun evaluate(expr: Expr): Any {
        return expr.accept(this)
    }

    private fun isBoolean(vararg things: Any) = things.all { it is Boolean }


    override
    fun visitBinaryExpression(expr: BinaryExpr): Any {
        val left = evaluate(expr.left)
        val right = evaluate(expr.right)
        when (expr.operand.type) {
            TokenType.PLUS -> return left as Double + right as Double
            TokenType.MINUS -> return left as Double - right as Double
            TokenType.SLASH -> return left as Double / right as Double
            TokenType.STAR -> return left as Double * right as Double
            TokenType.EQUAL_EQUAL -> return left == right
            TokenType.AND -> {
                if (isBoolean(left, right))
                    return left as Boolean && right as Boolean
            }
            TokenType.BANG_EQUAL -> return left != right
        }
        throw RuntimeException("${expr.operand.type}  not recognized")
    }

    override fun visitUnaryExpression(expr: UnaryExpr): Any {
        val l = evaluate(expr.left)
        when (expr.token.type) {
            TokenType.MINUS -> return -(l as Double)
        }
        TODO()
    }

    override fun visitLiteralExpression(expr: LiteralExpr): Any {
        return expr.token.literal
    }
}

fun main(args: Array<String>) {
    val toks = Scanner().tokenize("(9+-10)==-1")
    val ast = ExprParser(toks).parse()
    TreeWalker().evaluate(ast).also { println(it) }
}

typealias sourceCode = String
typealias lox = String

fun sourceCode.thing() {
}

fun stuff(s: lox) {
}



