package parser

import scanner.Tok
import scanner.TokenType


abstract class Expr {
    abstract fun <T> accept(visitor: Visitor<T>): T

}

class BinaryExpr(val left: Expr, val token: Tok, val right: Expr) : Expr() {
    override fun <T> accept(visitor: Visitor<T>): T {
        return visitor.visitBinaryExpression(this)
    }

    companion object {
        fun create(init: Builder.() -> Unit): BinaryExpr = Builder().apply(init).build()
    }

    class Builder {

        lateinit var left: Expr
        lateinit var right: Expr
        lateinit var token: Tok
        fun build() = BinaryExpr(left, token, right)

    }

}


class UnaryExpr(val token: Tok, val left: Expr) : Expr() {

    override fun <T> accept(visitor: Visitor<T>): T {
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

class LiteralExpr(val token: Tok) : Expr() {
    override fun <T> accept(visitor: Visitor<T>): T {
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
        left = LiteralExpr.create { token = Tok(TokenType.NUMBER, "3", "3", 1) }

        token = Tok(TokenType.MINUS, "-", "-", 1)


        right = BinaryExpr.create {
            left = LiteralExpr.create { token = Tok(TokenType.NUMBER, "5", 5, 0) }
            token = Tok(TokenType.MINUS, "*", "*", 1)
            right = LiteralExpr.create { token = Tok(TokenType.NUMBER, "5", 5, 0) }
        }
    }
    b.accept(Rpn()).also { println(it) }
}



