package com.github.navyazaveri.dynamik.stdlib

import com.github.navyazaveri.dynamik.expressions.*
import com.github.navyazaveri.dynamik.interpreter.Environment
import com.github.navyazaveri.dynamik.interpreter.TreeWalker
import kotlin.collections.List


//marker interface
interface Builtin

interface Container : Builtin {
    fun len(): Int
    override fun toString(): String
    fun toHash(): Int
}


abstract class ContainerInstance : Container, DynamikInstance() {
    /*
    Classes inherinting Container instance should just define relevant
  api calls
     */
    init {
        env.defineFunction("len", BuiltinCallable(this, "len", 0))
        env.defineFunction("toString", BuiltinCallable(this, "toString", 0))
        env.defineFunction("toHash", BuiltinCallable(this, "toHash", 0))
    }

}


class DynamikList : DynamikClass<ListInstance> {
    override fun invoke(arguments: List<Arg>, interpreter: TreeWalker, env: Environment): ListInstance {
        return ListInstance()
    }
}


class ListInstance : ContainerInstance() {
    override fun toHash(): Int {
        return _list.hashCode()
    }

    override fun toString(): String {
        return _list.toString()
    }

    override fun len(): Int {
        return _list.size
    }


    private val _list = mutableListOf<Any>()

    init {
        env.defineFunction("add", BuiltinCallable(this, "add", 1))
        env.defineFunction("get", BuiltinCallable(this, "get", 1))
        env.defineFunction("contains", BuiltinCallable(this, "contains", 1))
    }

    fun add(item: Any): Any {
        return _list.add(item)
    }

    fun get(i: Any): Any {
        return _list[(i as Double).toInt()]
    }

    fun contains(thing: Any): Boolean {
        return _list.contains(thing)
    }
}


class BuiltinCallable(val b: Builtin, val methodName: String, val arity: Int) : DynamikFunction<Any> {
    override fun invoke(arguments: List<Arg>, interpreter: TreeWalker, env: Environment): Any {
        val argTypes = (0 until arity).map { Any::class.java }.toTypedArray()
        return b::class.java.getMethod(methodName, *argTypes).invoke(b, *arguments.toTypedArray())
    }
}