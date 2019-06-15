package com.github.navyazaveri.dynamik.expressions


/**
 * A collection of methods a Dynamik Interpreter must implement
 *  @param T The type of the value returned by the Interpreter.
 */
interface ExpressionVisitor<T> {
    fun visitBinaryExpression(expr: BinaryExpr): T
    fun visitUnaryExpression(expr: UnaryExpr): T
    fun visitLiteralExpression(expr: LiteralExpr): T
    fun visitVariableExpr(variableExpr: VariableExpr): T
    fun visitCallExpression(callExpr: CallExpr, par: Boolean = false): T
    fun visitClazzExpression(instanceExpr: InstanceExpr): T
    fun visitThisExpr(thisExpr: ThisExpr): T
    fun visitAssignExpr(assignExpr: AssignExpr): T
    fun visitConcatExpr(concatExpr: ConcatExpr): T
}