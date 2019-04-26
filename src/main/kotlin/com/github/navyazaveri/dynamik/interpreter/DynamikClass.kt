package com.github.navyazaveri.dynamik.interpreter

import com.github.navyazaveri.dynamik.expressions.*


class DynamikInstance(val name: String, val functions: List<FnStmt>) : Callable {
    override fun invoke(arguments: List<Arg>, interpreter: TreeWalker, env: Environment): Any {
        return interpreter.env.get(name)
    }
    val env = Environment()

    init {
        functions.forEach {
            env.define(it.functionName.lexeme, DynamikCallable(it), VariableStatus.VAL)
        }
    }

    fun invokeMethod(name: String, args: List<Any>, interpreter: TreeWalker): Any {
        val c = env.get(name) as Callable
        return c.invoke(args, interpreter, this.env)
    }

}

fun main(args: Array<String>) {

}