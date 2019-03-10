package interpreter

import parser.BinaryExpr
import parser.Expr
import parser.LiteralExpr
import parser.UnaryExpr

interface Visitor<out T> {
    fun visitBinaryExpression(expr: BinaryExpr): T
    fun visitUnaryExpression(expr: UnaryExpr): T
    fun visitLiteralExpression(expr: LiteralExpr): T
}
