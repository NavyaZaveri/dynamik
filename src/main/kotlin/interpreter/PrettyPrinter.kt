package interpreter

import expressions.*

abstract class PrettyPrinter : ExpressionVisitor<String>, StatementVisitor<String> {
    fun prettyPrint(expr: Expr): String = expr.evaluateBy(this)

    override fun visitWaitStmt(waitStmt: WaitStmt): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun visitParStatement(parStmt: ParStmt): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun visitReturnStatement(returnStmt: ReturnStmt): String {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }

    abstract fun wrap(operand: String, vararg exprs: Expr): String

    override fun visitFnStatement(fnStmt: FnStmt): String {
        TODO()
    }

    override fun visitIfStmt(ifStmt: IfStmt): String {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }

    override fun visitCallExpression(callExpr: CallExpr, par: Boolean): String {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }

    override fun visitWhileStatement(whileStmt: WhileStmt): String =
        "while " + prettyPrint(whileStmt.expr) + "{ " +
                whileStmt.stmts.joinToString(
                    prefix = "",
                    postfix = "",
                    separator = "\n"
                ) { it.evaluateBy(this) + ";" } + " }"

    override fun visitBinaryExpression(expr: BinaryExpr): String = wrap(expr.operand.lexeme, expr.left, expr.right)

    override fun visitVariableExpr(variableExpr: VariableExpr): String = variableExpr.token.lexeme

    override fun visitAssignStmt(assignStmt: AssignStmt) =
        "${assignStmt.token.lexeme} = " + prettyPrint(assignStmt.expr)

    override fun visitLiteralExpression(expr: LiteralExpr): String = expr.token.literal.toString()

    override fun visitUnaryExpression(expr: UnaryExpr): String = wrap(expr.token.lexeme, expr.left)

    override fun visitPrintStmt(printStmt: PrintStmt): String {
        return "print " + prettyPrint(printStmt.expr)
    }

    override fun visitValStmt(valStmt: ValStmt): String = "val ${valStmt.name.lexeme} = " + prettyPrint(valStmt.expr)

    override fun visitVariableStmt(varStmt: VarStmt): String =
        "var ${varStmt.name.lexeme} = " + prettyPrint(varStmt.expr)

    override fun visitExpressionsStatement(exprStmt: ExprStmt): String = prettyPrint(exprStmt.expr)
}

// A reversed polish notation based pretty printer
class Rpn : PrettyPrinter() {

    override fun wrap(operand: String, vararg exprs: Expr): String {
        var res = ""
        for (expr in exprs) {
            res += " " + prettyPrint(expr)
        }
        return "$res $operand".trim()
    }
}
