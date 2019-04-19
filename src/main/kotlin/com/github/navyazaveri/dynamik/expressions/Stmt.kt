package com.github.navyazaveri.dynamik.expressions

import com.github.navyazaveri.dynamik.scanner.Tok

abstract class Stmt {
    abstract fun <T> evaluateBy(visitor: StatementVisitor<T>): T
}

class PrintStmt(val expr: Expr) : Stmt() {
    override fun <T> evaluateBy(visitor: StatementVisitor<T>): T = visitor.visitPrintStmt(this)
}

class AssignStmt(val token: Tok, val expr: Expr) : Stmt() {
    override fun <T> evaluateBy(visitor: StatementVisitor<T>): T = visitor.visitAssignStmt(this)
}

class WhileStmt(val expr: Expr, val stmts: List<Stmt>) : Stmt() {
    override fun <T> evaluateBy(visitor: StatementVisitor<T>): T {
        return visitor.visitWhileStatement(this)
    }
}

class ForStmt(val init: Stmt, val condition: Expr, val body: List<Stmt>) : Stmt() {
    override fun <T> evaluateBy(visitor: StatementVisitor<T>): T {
        // desugaring into while loops
        init.evaluateBy(visitor)
        return WhileStmt(condition, body).evaluateBy(visitor)
    }
}

class ValStmt(val name: Tok, val expr: Expr) : Stmt() {
    override fun <T> evaluateBy(visitor: StatementVisitor<T>): T = visitor.visitValStmt(this)
}

class ExprStmt(val expr: Expr) : Stmt() {
    override fun <T> evaluateBy(visitor: StatementVisitor<T>): T = visitor.visitExpressionsStatement(this)
}

class VarStmt(val name: Tok, val expr: Expr) : Stmt() {
    override fun <T> evaluateBy(visitor: StatementVisitor<T>): T = visitor.visitVariableStmt(this)
}

class FnStmt(val functionName: Tok, val params: List<Tok>, val body: List<Stmt>, val memoize: Boolean = false) :
    Stmt() {
    override fun <T> evaluateBy(visitor: StatementVisitor<T>): T {
        return visitor.visitFnStatement(this)
    }
}

class ReturnStmt(val statement: Stmt) : Stmt() {
    override fun <T> evaluateBy(visitor: StatementVisitor<T>): T {
        return visitor.visitReturnStatement(this)
    }
}

class IfStmt(val condition: Expr, val body: List<Stmt>, val elseBody: List<Stmt> = mutableListOf()) : Stmt() {
    override fun <T> evaluateBy(visitor: StatementVisitor<T>): T {
        return visitor.visitIfStmt(this)
    }
}

class ParStmt(val callExpr: CallExpr) : Stmt() {
    override fun <T> evaluateBy(visitor: StatementVisitor<T>): T {
        return visitor.visitParStatement(this)
    }
}

class WaitStmt : Stmt() {
    override fun <T> evaluateBy(visitor: StatementVisitor<T>): T {
        return visitor.visitWaitStmt(this)
    }
}

class GlobalStmt(val name: Tok, val value: Any) : Stmt() {
    override fun <T> evaluateBy(visitor: StatementVisitor<T>): T {
        return visitor.visitGlobalStmt(this)
    }

}


interface StatementVisitor<out T> {
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
}
