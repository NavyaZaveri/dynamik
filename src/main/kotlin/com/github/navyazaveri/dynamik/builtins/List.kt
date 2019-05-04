package com.github.navyazaveri.dynamik.builtins

import com.github.navyazaveri.dynamik.expressions.Arg
import com.github.navyazaveri.dynamik.expressions.Callable
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


class List : Callable<Builtin> {
    override fun invoke(arguments: List<Arg>, interpreter: TreeWalker, env: Environment): ListInstance {
        return ListInstance();
    }
}


class ListInstance : Builtin {
    val env = Environment()
    private val backingList = mutableListOf<Any>();

    init {
        env.defineFunction("add", BuiltinCallable<Any>(this, "add", 0))
        env.defineFunction("get", BuiltinCallable<Any>(this, "get0", 1))
    }

    fun add(item: Any) {
        backingList.add(item);
    }

    fun get(i: Int): Any {
        return backingList[i]
    }
}

class BuiltinCallable<T : Any>(val b: Builtin, val methodName: String, val arity: Int) : Callable<T> {
    override fun invoke(arguments: List<Arg>, interpreter: TreeWalker, env: Environment): T {
        val argTypes = (0 until arity).map { Any::class.java }.toTypedArray()
        return b::class.java.getMethod(methodName, *argTypes).invoke(b, *arguments.toTypedArray()) as T
    }
}