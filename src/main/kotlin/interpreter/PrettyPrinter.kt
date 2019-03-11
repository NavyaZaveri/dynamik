package interpreter

import expressions.BinaryExpr
import expressions.Expr
import expressions.LiteralExpr
import expressions.UnaryExpr


abstract class PrettyPrinter : Visitor<String> {
    fun prettyPrint(expr: Expr): String {
        return expr.accept(this)
    }

    abstract fun wrap(operand: String, vararg exprs: Expr): String

    override fun visitBinaryExpression(expr: BinaryExpr): String {
        return wrap(expr.operand.lexeme, expr.left, expr.right)
    }

    override fun visitLiteralExpression(expr: LiteralExpr): String {
        return expr.token.literal.toString()
    }

    override fun visitUnaryExpression(expr: UnaryExpr): String {
        return wrap(expr.token.lexeme, expr.left)
    }
}


//A reversed polish notation based pretty printer
class Rpn : PrettyPrinter() {
    override fun wrap(operand: String, vararg exprs: Expr): String {
        var res = ""
        for (expr in exprs) {
            res += " " + prettyPrint(expr)
        }
        return "$res $operand".trim()
    }
}
