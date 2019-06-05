package com.github.navyazaveri.dynamik.expressions


/**
 * A collection of methods a Dynamik Interpreter must implement.
 *  @param T The type of the value returned by the Interpreter.
 */

interface StatementVisitor<T> {
    fun visitPrintStmt(printStmt: PrintStmt): T
    fun visitVariableStmt(varStmt: VarStmt): T
    fun visitValStmt(valStmt: ValStmt): T
    fun visitAssignStmt(assignStmt: AssignStmt): T
    fun visitExpressionsStatement(exprStmt: ExprStmt): T
    fun visitWhileStatement(whileStmt: WhileStmt): T
    fun visitFnStatement(fnStmt: FnStmt): T
    fun visitIfStmt(ifStmt: IfStmt): T
    fun visitReturnStatement(returnStmt: ReturnStmt): T
    fun visitParStatement(parStmt: ParStmt): T
    fun visitWaitStmt(waitStmt: WaitStmt): T
    fun visitGlobalStmt(globalStmt: GlobalStmt): T
    fun visitAssertStmt(assertStmt: AssertStmt): T
    fun visitSkipStatement(skipStmt: SkipStmt): T
    fun visitClassStmt(classStmt: ClassStmt): T
    fun visitInstanceStmt(instanceStmt: InstanceStmt): T
    fun visitThisStmt(thisStmt: ThisStmt): T
    fun visitChainedStmt(chainedStmt: ChainedStmt): T
}