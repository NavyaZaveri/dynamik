package expressions

import scanner.Tok

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

class ValStmt(val name: Tok, val expr: Expr) : Stmt() {
    override fun <T> evaluateBy(visitor: StatementVisitor<T>): T = visitor.visitValStmt(this)
}

class ExprStmt(val expr: Expr) : Stmt() {
    override fun <T> evaluateBy(visitor: StatementVisitor<T>): T = visitor.visitExpressionsStatement(this)
}

class VarStmt(val name: Tok, val expr: Expr) : Stmt() {
    override fun <T> evaluateBy(visitor: StatementVisitor<T>): T = visitor.visitVariableStmt(this)
}

interface StatementVisitor<out T> {
    fun visitPrintStmt(printStmt: PrintStmt): T
    fun visitVariableStmt(varStmt: VarStmt): T
    fun visitValStmt(valStmt: ValStmt): T
    fun visitAssignStmt(assignStmt: AssignStmt): T
    fun visitExpressionsStatement(exprStmt: ExprStmt): T
    fun visitWhileStatement(whileStmt: WhileStmt): T
}

