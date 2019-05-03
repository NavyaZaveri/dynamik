package com.github.navyazaveri.dynamik.interpreter

import com.github.navyazaveri.dynamik.errors.InvalidContructorArgs
import com.github.navyazaveri.dynamik.expressions.*


class DynamikClass(val name: String, val functions: List<FnStmt>, val params: List<String>) : Callable {
    override fun invoke(arguments: List<Arg>, interpreter: TreeWalker, env: Environment): Any {
        if (params.size != arguments.size) {
            throw InvalidContructorArgs(fname = name, expected = params.size, actual = arguments.size)
        }
        return DynamikInstance(name, functions, params.zip(arguments).toMap(), interpreter = interpreter)
    }

    override fun toString(): String {
        return "$name class"
    }
}


class DynamikInstance(
    val name: String,
    val functions: List<FnStmt>,
    val fields: Map<String, Any>,
    val interpreter: TreeWalker
) {
    val env = Environment()

    init {

        //define methods
        functions.forEach {
            env.define(it.functionName.lexeme, DynamikCallable(it), VariableStatus.VAL)
        }

        fields.forEach { t, u -> env.defineField(t, u) }
        interpreter.env.classes.forEach { t, u -> env.defineClass(t, u.value as DynamikClass) }
    }


    fun invokeMethod(name: String, args: List<Any>, interpreter: TreeWalker): Any {
        val c = env.get(name) as Callable
        return c.invoke(args, interpreter, this.env)
    }

    override fun toString(): String {
        return name + " instance"
    }
}
