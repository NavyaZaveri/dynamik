package interpreter

import expressions.*


abstract class PrettyPrinter : ExpressionVisitor<String>, StatementVisitor<String> {
    fun prettyPrint(expr: Expr): String {
        return expr.accept(this)
    }

    abstract fun wrap(operand: String, vararg exprs: Expr): String

    override fun visitBinaryExpression(expr: BinaryExpr): String {
        return wrap(expr.operand.lexeme, expr.left, expr.right)
    }

    override fun visitVariableExpr(variableExpr: VariableExpr): String {
        return variableExpr.token.lexeme
    }

    override fun visitAssignStmt(assignStmt: AssignStmt): String {
        return "${assignStmt.token.lexeme} = " + prettyPrint(assignStmt.expr)
    }

    override fun visitLiteralExpression(expr: LiteralExpr): String {
        return expr.token.literal.toString()
    }

    override fun visitUnaryExpression(expr: UnaryExpr): String {
        return wrap(expr.token.lexeme, expr.left)
    }

    override fun visitPrintStmt(printStmt: PrintStmt): String {
        return "print " + prettyPrint(printStmt.expr)
    }

    override fun visitValStmt(valStmt: ValStmt): String {
        return "val ${valStmt.name.lexeme} = " + prettyPrint(valStmt.expr)
    }

    override fun visitVariableStmt(varStmt: VarStmt): String {
        return "var ${varStmt.name.lexeme} = " + prettyPrint(varStmt.expr)
    }

    override fun visitExpressionsStatement(exprStmt: ExprStmt): String {
        return prettyPrint(exprStmt.expr)
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
