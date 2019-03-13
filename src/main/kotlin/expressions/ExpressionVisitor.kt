package expressions

interface ExpressionVisitor<out T> {
    fun visitBinaryExpression(expr: BinaryExpr): T
    fun visitUnaryExpression(expr: UnaryExpr): T
    fun visitLiteralExpression(expr: LiteralExpr): T
    fun visitVariableExpr(variableExpr: VariableExpr): T
}

