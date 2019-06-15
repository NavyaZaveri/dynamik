package com.github.navyazaveri.dynamik.expressions

import com.github.navyazaveri.dynamik.errors.InvalidConstructorArgSize
import com.github.navyazaveri.dynamik.interpreter.Environment
import com.github.navyazaveri.dynamik.interpreter.TreeWalker

class DefaultClass(val name: String, val functions: List<FnStmt>, val params: List<String>) :
    DynamikClass<DefaultInstance> {
    override fun invoke(arguments: List<Arg>, interpreter: TreeWalker, env: Environment): DefaultInstance {
        if (params.size != arguments.size) {
            throw InvalidConstructorArgSize(fname = name, expected = params.size, actual = arguments.size)
        }
        return DefaultInstance(
            name,
            functions,
            params.zip(arguments).toMap(),
            interpreter = interpreter
        )
    }

    override fun toString(): String {
        return "$name class"
    }
}


class DefaultInstance(
    val name: String,
    functions: List<FnStmt>,
    fields: Map<String, Any>,
    val interpreter: TreeWalker
) : DynamikInstance() {
    override fun toHash(): Int {
        return this.toHash()
    }

    init {

        //define methods
        functions.forEach {
            env.defineFunction(it.functionName.lexeme, DefaultFunction(it))
        }

        //set fields
        fields.forEach { (t, u) -> env.defineField(t, u) }

        //put outer classes in scope
        interpreter.env.classes().forEach { (t, u) -> env.defineClass(t, u.value) }
    }

    override fun toString(): String {
        return name + " instance"
    }
}
