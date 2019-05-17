package com.github.navyazaveri.dynamik.expressions

import com.github.navyazaveri.dynamik.scanner.Tok
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

abstract class Stmt {
    abstract fun <T> evaluateBy(visitor: StatementVisitor<T>): T
}

class PrintStmt(val stmt: Stmt) : Stmt() {
    override fun <T> evaluateBy(visitor: StatementVisitor<T>): T = visitor.visitPrintStmt(this)
}

class AssignStmt(val token: Tok, val expr: Expr) : Stmt() {
    override fun <T> evaluateBy(visitor: StatementVisitor<T>): T = visitor.visitAssignStmt(this)
}

class WhileStmt(val expr: Expr, val stmts: List<Stmt>) : Stmt() {
    override fun <T> evaluateBy(visitor: StatementVisitor<T>): T = visitor.visitWhileStatement(this)
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
    override fun <T> evaluateBy(visitor: StatementVisitor<T>): T = visitor.visitFnStatement(this)
}

class SkipStmt : Stmt() {
    override fun <T> evaluateBy(visitor: StatementVisitor<T>): T {
        return visitor.visitSkipStatement(this)
    }
}

class ReturnStmt(val statement: Stmt) : Stmt() {
    override fun <T> evaluateBy(visitor: StatementVisitor<T>): T = visitor.visitReturnStatement(this)
}

class IfStmt(val condition: Expr, val body: List<Stmt>, val elseBody: List<Stmt> = mutableListOf()) : Stmt() {
    override fun <T> evaluateBy(visitor: StatementVisitor<T>): T = visitor.visitIfStmt(this)
}

class ParStmt(val callExpr: CallExpr, val lock: Boolean = false) : Stmt() {
    override fun <T> evaluateBy(visitor: StatementVisitor<T>): T = visitor.visitParStatement(this)
}

class WaitStmt : Stmt() {
    override fun <T> evaluateBy(visitor: StatementVisitor<T>): T {
        GlobalScope.launch { visitor.visitWaitStmt(this@WaitStmt) }
        return visitor.visitSkipStatement(SkipStmt())
    }
}

class GlobalStmt(val name: Tok, val value: Expr) : Stmt() {
    override fun <T> evaluateBy(visitor: StatementVisitor<T>): T = visitor.visitGlobalStmt(this)
}

class AssertStmt(val e1: Expr) : Stmt() {
    override fun <T> evaluateBy(visitor: StatementVisitor<T>): T = visitor.visitAssertStmt(this)
}

class InstanceStmt(val name: String, val stmt: Stmt) : Stmt() {
    override fun <T> evaluateBy(visitor: StatementVisitor<T>): T {
        return visitor.visitInstanceStmt(this)
    }
}

class ThisStmt(val stmt: Stmt) : Stmt() {
    override fun <T> evaluateBy(visitor: StatementVisitor<T>): T {
        return visitor.visitThisStmt(this);
    }
}


class ClassStmt(
    val name: String,
    val methods: List<FnStmt> = mutableListOf(),
    val fields: List<String> = mutableListOf()
) : Stmt() {
    override fun <T> evaluateBy(visitor: StatementVisitor<T>): T {
        return visitor.visitClassStmt(this)
    }
}
