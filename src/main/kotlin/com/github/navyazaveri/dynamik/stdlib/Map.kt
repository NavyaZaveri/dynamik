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
    }

    val backingMap = mutableMapOf<Any, Any>()
    override fun toHash(): Int {
        return backingMap.hashCode()
    }

    override fun toString(): String {
        return backingMap.toString()
    }

    override fun len(): Int {
        return backingMap.size
    }

    fun insert(k: Any, v: Any) {
        backingMap[k] = v;
    }

    fun get(k: Any): Any {
        return backingMap[k]!!
    }
}