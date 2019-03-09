package parser

import scanner.Tok


abstract class Expr {
    abstract fun <T> accept(visitor: T): T
}

class BinaryExpr(val left: Expr, val token: Tok, val right: Expr) : Expr() {
    override fun <T> accept(visitor: T): T {
        TODO("not implemented") //To change body gof created functions use File | Settings | File Templates.
    }
}


class UnaryExpr(val token: Tok, val left: Expr) : Expr() {
    override fun <T> accept(visitor: T): T {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}

class LiteralExpr(val token: Tok) : Expr() {
    override fun <T> accept(visitor: T): T {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}



