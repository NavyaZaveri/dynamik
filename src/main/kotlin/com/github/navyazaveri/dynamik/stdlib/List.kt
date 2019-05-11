package com.github.navyazaveri.dynamik.stdlib

import com.github.navyazaveri.dynamik.expressions.*
import com.github.navyazaveri.dynamik.interpreter.Environment
import com.github.navyazaveri.dynamik.interpreter.TreeWalker
import kotlin.collections.List


interface Builtin


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

    override fun len(): Double {
        return _list.size.toDouble()
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

