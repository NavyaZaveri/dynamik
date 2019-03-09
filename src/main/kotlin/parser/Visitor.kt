package parser

interface Visitor<T> {
    fun visitBinaryExpression(expr: BinaryExpr): T
    fun visitUnaryExpression(expr: UnaryExpr): T
    fun visitLiteralExpresion(expr: LiteralExpr): T
}

class RPN : Visitor<String> {
    override fun visitUnaryExpression(expr: UnaryExpr): String {
        TODO()
    }

    override fun visitLiteralExpresion(expr: LiteralExpr): String {
        return expr.token.literal.toString()
    }

    override fun visitBinaryExpression(expr: BinaryExpr): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}