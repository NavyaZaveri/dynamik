package com.github.navyazaveri.dynamik.stdlib

import com.github.navyazaveri.dynamik.expressions.Arg
import com.github.navyazaveri.dynamik.expressions.DynamikClass
import com.github.navyazaveri.dynamik.interpreter.Environment
import com.github.navyazaveri.dynamik.interpreter.TreeWalker


class DynamikMap : DynamikClass<MapInstance> {
    override fun invoke(arguments: List<Arg>, interpreter: TreeWalker, env: Environment): MapInstance {
        return MapInstance()
    }
}

class MapInstance : ContainerInstance() {
    init {
        env.defineFunction("insert", BuiltinCallable(this, "len", 2))
        env.defineFunction("get", BuiltinCallable(this, "get", 1))
        env.defineFunction("contains", BuiltinCallable(this, "contains", 1))

    }

    val _map = mutableMapOf<Any, Any>()
    override fun toHash(): Int {
        return _map.hashCode()
    }

    override fun toString(): String {
        return _map.toString()
    }

    override fun len(): Double {
        return _map.size.toDouble()
    }

    fun insert(k: Any, v: Any) {
        _map[k] = v
    }

    fun get(k: Any): Any {
        return _map[k]!!
    }

    fun contains(item: Any): Boolean {
        return _map.contains(item)
    }
}