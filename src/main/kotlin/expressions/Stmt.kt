package expressions

abstract class Stmt {
    abstract fun <T> accept(visitor: StatementVisitor<T>): T
}

class PrintStmt(val expr: Expr) : Stmt() {
    override fun <T> accept(visitor: StatementVisitor<T>): T {
        return visitor.visitPrintStmt(this)
    }
}


class ValStmt(val name: String, val expr: Expr) : Stmt() {
    override fun <T> accept(visitor: StatementVisitor<T>): T {
        return visitor.visitValStmt(this)
    }
}


class VarStmt(val name: String, val expr: Expr) : Stmt() {
    override fun <T> accept(visitor: StatementVisitor<T>): T {
        return visitor.visitVariableStmt(this)
    }
}

interface StatementVisitor<out T> {
    fun visitPrintStmt(printStmt: PrintStmt): T
    fun visitVariableStmt(varStmt: VarStmt): T
    fun visitValStmt(varStmt: ValStmt): T
}