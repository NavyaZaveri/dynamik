package expressions

import interpreter.Rpn
import scanner.Tok
import scanner.TokenType


abstract class Expr {
    abstract fun <T> evaluateBy(visitor: ExpressionVisitor<T>): T
}


class BinaryExpr(val left: Expr, val operand: Tok, val right: Expr) : Expr() {
    override fun <T> evaluateBy(visitor: ExpressionVisitor<T>): T {
        return visitor.visitBinaryExpression(this)
    }

    companion object {
        fun create(init: Builder.() -> Unit): BinaryExpr = Builder().apply(init).build()
    }

    class Builder {
        lateinit var left: Expr
        lateinit var right: Expr
        lateinit var operand: Tok
        fun build() = BinaryExpr(left, operand, right)
    }
}

class UnaryExpr(val token: Tok, val left: Expr) : Expr() {

    override fun <T> evaluateBy(visitor: ExpressionVisitor<T>): T {
        return visitor.visitUnaryExpression(this)
    }

    class Builder {
        lateinit var left: Expr
        lateinit var token: Tok
        fun build(): UnaryExpr = UnaryExpr(token, left)
    }

    companion object {
        fun create(init: Builder.() -> Unit) = Builder().apply(init).build()
    }
}

class VariableExpr(val token: Tok) : Expr() {
    override fun <T> evaluateBy(visitor: ExpressionVisitor<T>): T {
        return visitor.visitVariableExpr(this)
    }

}

class LiteralExpr(val token: Tok) : Expr() {
    override fun <T> evaluateBy(visitor: ExpressionVisitor<T>): T {
        return visitor.visitLiteralExpression(this)
    }

    class Builder {
        lateinit var token: Tok
        fun build(): LiteralExpr = LiteralExpr(token)
    }

    companion object {
        fun create(init: Builder.() -> Unit) = Builder().apply(init).build()
    }
}

fun main(args: Array<String>) {
    val b = BinaryExpr.create {
        left = LiteralExpr.create { token = Tok(TokenType.NUMBER, "3", "3") }
        operand = Tok(TokenType.MINUS, "-", "-", 1)
        right = BinaryExpr.create {
            left = LiteralExpr.create { token = Tok(TokenType.NUMBER, "5", 5.0) }
            operand = Tok(TokenType.MINUS, "*", "*")
            right = BinaryExpr.create {
                left = LiteralExpr.create { token = Tok(TokenType.NUMBER, "100", 100.0, 0) }
                operand = Tok(TokenType.MINUS, "-", "-")
                right = LiteralExpr.create { token = Tok(TokenType.NUMBER, "200", 200.0, 0) }
            }
        }
    }
    b.evaluateBy(Rpn()).also { println(it) }
}



