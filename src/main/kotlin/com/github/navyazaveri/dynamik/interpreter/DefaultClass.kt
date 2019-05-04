package com.github.navyazaveri.dynamik.interpreter

import com.github.navyazaveri.dynamik.errors.InvalidConstructorArgSize
import com.github.navyazaveri.dynamik.expressions.*


class DefaultClass(val name: String, val functions: List<FnStmt>, val params: List<String>) :
    DynamikClass<DefaultInstance> {
    override fun invoke(arguments: List<Arg>, interpreter: TreeWalker, env: Environment): DefaultInstance {
        if (params.size != arguments.size) {
            throw InvalidConstructorArgSize(fname = name, expected = params.size, actual = arguments.size)
        }
        return DefaultInstance(name, functions, params.zip(arguments).toMap(), interpreter = interpreter)
    }

    override fun toString(): String {
        return "$name class"
    }
}


class DefaultInstance(
    val name: String,
    val functions: List<FnStmt>,
    val fields: Map<String, Any>,
    val interpreter: TreeWalker
) : DynamikInstance() {

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
