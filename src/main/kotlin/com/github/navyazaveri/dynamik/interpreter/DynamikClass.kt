package com.github.navyazaveri.dynamik.interpreter

import com.github.navyazaveri.dynamik.errors.InvalidConstructorArgSize
import com.github.navyazaveri.dynamik.expressions.*


class DynamikClass(val name: String, val functions: List<FnStmt>, val params: List<String>) :
    Callable<DynamikInstance> {
    override fun invoke(arguments: List<Arg>, interpreter: TreeWalker, env: Environment): DynamikInstance {
        if (params.size != arguments.size) {
            throw InvalidConstructorArgSize(fname = name, expected = params.size, actual = arguments.size)
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
            env.defineFunction(it.functionName.lexeme, DefaultFunction(it))
        }

        fields.forEach { t, u -> env.defineField(t, u) }
        interpreter.env.classes().forEach { t, u -> env.defineClass(t, u.value) }
    }


    fun invokeMethod(name: String, args: List<Any>, interpreter: TreeWalker): Any {
        val c = env.getCallable(name)
        return c.invoke(args, interpreter, this.env)
    }

    override fun toString(): String {
        return name + " instance"
    }
}
