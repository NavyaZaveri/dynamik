package errors

import expressions.LiteralExpr

abstract class Thing {

    abstract fun blah()

}

class Stuff : Thing() {
    override fun blah() {
        println("im a sutff")
    }

}

class Woo : Thing() {
    override fun blah() {
        println("woot woot ")
    }

}

fun <T> foo2(factory: () -> T): T {
    var entity: T = factory()
    return entity
}

