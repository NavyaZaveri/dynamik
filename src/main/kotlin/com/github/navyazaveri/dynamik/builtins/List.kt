package com.github.navyazaveri.dynamik.builtins

import com.github.navyazaveri.dynamik.expressions.Arg
import com.github.navyazaveri.dynamik.expressions.Callable
import com.github.navyazaveri.dynamik.expressions.DynamikFunction
import com.github.navyazaveri.dynamik.interpreter.Environment
import com.github.navyazaveri.dynamik.interpreter.TreeWalker
import kotlin.collections.List


interface DynInstance {
    fun runMethod(d: DynInstance, methodName: String, args: List<Arg>): Any
}

//marker interface
interface Builtin

interface Container : Builtin {
    fun size()
}


class List : Callable<ListInstance> {
    override fun invoke(arguments: List<Arg>, interpreter: TreeWalker, env: Environment): ListInstance {
        return ListInstance()
    }
}


class ListInstance : Builtin {
    val env = Environment()
    private val backingList = mutableListOf<Any>();

    init {
        env.defineFunction("add", BuiltinCallable(this, "add", 0))
        env.defineFunction("get", BuiltinCallable(this, "get", 1))
    }

    fun add(item: Any) {
        backingList.add(item);
    }

    fun get(i: Int): Any {
        return backingList[i]
    }
}

class BuiltinCallable(val b: Builtin, val methodName: String, val arity: Int) : DynamikFunction<Any> {
    override fun invoke(arguments: List<Arg>, interpreter: TreeWalker, env: Environment): Any {
        val argTypes = (0 until arity).map { Any::class.java }.toTypedArray()
        return b::class.java.getMethod(methodName, *argTypes).invoke(b, *arguments.toTypedArray())!!
    }
}