package interpreter

import parser.BinaryExpr
import parser.Expr
import parser.LiteralExpr
import parser.UnaryExpr

interface Visitor<T> {
    fun visitBinaryExpression(expr: BinaryExpr): T
    fun visitUnaryExpression(expr: UnaryExpr): T
    fun visitLiteralExpression(expr: LiteralExpr): T
}

//A reversed polish notation based pretty printer
class Rpn : Visitor<String> {
    override fun visitUnaryExpression(expr: UnaryExpr): String {
        return wrap(expr.token.lexeme, expr.left)
    }

    override fun visitLiteralExpression(expr: LiteralExpr): String {
        return expr.token.literal.toString()
    }

    override fun visitBinaryExpression(expr: BinaryExpr): String {
        return wrap(expr.operand.lexeme, expr.left, expr.right)
    }

    private fun wrap(name: String, vararg exprs: Expr): String {
        var res = "($name"
        for (expr in exprs) {
            res += " " + expr.accept(this)
        }
        return "$res)"
    }

    fun prettyPrint(expr: Expr): String {
        return expr.accept(this)
    }
}
