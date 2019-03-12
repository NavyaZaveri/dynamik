package interpreter

import expressions.BinaryExpr
import expressions.LiteralExpr
import expressions.UnaryExpr

interface ExpressionVisitor<out T> {
    fun visitBinaryExpression(expr: BinaryExpr): T
    fun visitUnaryExpression(expr: UnaryExpr): T
    fun visitLiteralExpression(expr: LiteralExpr): T
}

