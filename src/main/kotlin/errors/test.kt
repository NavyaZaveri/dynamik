package errors

import expressions.LiteralExpr

abstract class Thing {
    abstract fun blah()
}

class UnexpectedType(message: String) : Exception(message)
class VariableNotInScope(message: String) : Exception(message)
class ValError(message: String = "cannot reassasign") : Exception(message)



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

fun main(args: Array<String>) {

    try {
        throw VariableNotInScope("not ins scope")
    } catch (v: VariableNotInScope) {
        println(v.message)
    }
}
