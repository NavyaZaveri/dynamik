package com.github.navyazaveri.dynamik.stdlib

import com.github.navyazaveri.dynamik.expressions.Arg
import com.github.navyazaveri.dynamik.expressions.DynamikClass
import com.github.navyazaveri.dynamik.interpreter.Environment
import com.github.navyazaveri.dynamik.interpreter.TreeWalker


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
        env.defineFunction("add", NativeCallable("list.add", 1) { _list.add(it[0]) })
        env.defineFunction("get", NativeCallable("list.get", 1) { _list[(it[0] as Double).toInt()] })
        env.defineFunction("contains", NativeCallable("list.contains", 1) { _list.contains(it[0]) })
    }

    fun contains(thing: Any): Boolean {
        return _list.contains(thing)
    }
}


