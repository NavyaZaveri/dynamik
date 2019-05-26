package com.github.navyazaveri.dynamik.expressions

import com.github.navyazaveri.dynamik.scanner.Tok


abstract class Expr {
    abstract fun <T> evaluateBy(visitor: ExpressionVisitor<T>): T
}


/**
 * [CallExpr] captures function and instance invocations
 */
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

class ThisExpr(val expr: Expr) : Expr() {
    override fun <T> evaluateBy(visitor: ExpressionVisitor<T>): T {
        return visitor.visitThisExpr(this)
    }
}

class AssignExpr(val tok: Tok, val expr: Expr) : Expr() {
    override fun <T> evaluateBy(visitor: ExpressionVisitor<T>): T {
        return visitor.visitAssignExpr(this)
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

class InstanceExpr(val clazzName: String, val expr: Expr) : Expr() {
    override fun <T> evaluateBy(visitor: ExpressionVisitor<T>): T {
        return visitor.visitClazzExpression(this)
    }
}

class ConcatExpr(val containers: List<Expr>) : Expr() {
    override fun <T> evaluateBy(visitor: ExpressionVisitor<T>): T {
        return visitor.visitConcatExpr(this)
    }
}

