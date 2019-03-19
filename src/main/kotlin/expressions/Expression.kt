package expressions

import scanner.Tok


abstract class Expr {
    abstract fun <T> evaluateBy(visitor: ExpressionVisitor<T>): T
}

class CallExpr(val funcName: String, val args: List<Expr>) : Expr() {
    override fun <T> evaluateBy(visitor: ExpressionVisitor<T>): T {
        return visitor.visitCallExpression(this)
    }

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


