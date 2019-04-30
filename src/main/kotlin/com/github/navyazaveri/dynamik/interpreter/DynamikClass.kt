package com.github.navyazaveri.dynamik.interpreter

import com.github.navyazaveri.dynamik.errors.InvalidArgSize
import com.github.navyazaveri.dynamik.expressions.*


class DynamikClass(val name: String, val functions: List<FnStmt>, val params: List<String>) : Callable {
    override fun invoke(arguments: List<Arg>, interpreter: TreeWalker, env: Environment): Any {
        if (params.size != arguments.size) {
            throw InvalidArgSize(fname = name, expected = params.size, actual = arguments.size)
        }
        return DynamikInstance(name, functions, params.zip(arguments).toMap())
    }
}


class DynamikInstance(val name: String, val functions: List<FnStmt>, val fields: Map<String, Any>) {
    val env = Environment()

    init {

        //define methods
        functions.forEach {
            env.define(it.functionName.lexeme, DynamikCallable(it), VariableStatus.VAL)
        }

        //define fields
        fields.forEach { t, u -> env.define(t, u, VariableStatus.VAR) }
    }


    fun invokeMethod(name: String, args: List<Any>, interpreter: TreeWalker): Any {
        val c = env.get(name) as Callable
        /*
        algo for scope resolution: Keep track of CURRENT instance variables
        Then execute the methods. New variables might have been injected into the envinronmet
        Remove those, whilst preserving the mutates instance variables
         */
        return c.invoke(args, interpreter, this.env)
    }
}

fun main(args: Array<String>) {
}