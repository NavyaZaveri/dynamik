package com.github.navyazaveri.dynamik.expressions

interface ExpressionVisitor<T> {
    fun visitBinaryExpression(expr: BinaryExpr): T
    fun visitUnaryExpression(expr: UnaryExpr): T
    fun visitLiteralExpression(expr: LiteralExpr): T
    fun visitVariableExpr(variableExpr: VariableExpr): T
    fun visitCallExpression(callExpr: CallExpr, par: Boolean = false): T
    fun visitClazzExpression(instanceExpr: InstanceExpr): T
}
