package com.github.navyazaveri.dynamik.stdlib

import com.github.navyazaveri.dynamik.errors.InvalidArgSize
import com.github.navyazaveri.dynamik.expressions.Arg
import com.github.navyazaveri.dynamik.expressions.DynamikClass
import com.github.navyazaveri.dynamik.expressions.DynamikFunction
import com.github.navyazaveri.dynamik.interpreter.Environment
import com.github.navyazaveri.dynamik.interpreter.TreeWalker


class DynamikMap : DynamikClass<MapInstance> {
    override fun invoke(arguments: List<Arg>, interpreter: TreeWalker, env: Environment): MapInstance {
        return MapInstance()
    }
}

class MapInstance : ContainerInstance() {
    val _map = java.util.HashMap<Any, Any>()

    override fun equals(other: Any?): Boolean {
        return (other is MapInstance) && this._map == other._map
    }

    init {
        env.defineFunction("clear", NativeCallable("map.clear", 0) { _map.clear() })
        env.defineFunction("insert", NativeCallable("map.insert", 2) { _map[it[0]] = it[1] })
        env.defineFunction("get", NativeCallable("map.get", 1) { _map[it[0]]!! })
        env.defineFunction("contains", NativeCallable("map.contains", 1) { _map.containsKey(it[0]) })
    }

    override fun toHash(): Int {
        return _map.hashCode()
    }

    override fun toString(): String {
        return _map.toString()
    }
}

/**
 * A simple FFI that allows kotlin to power some of Dynamik's builtin
 * functions
 */
class NativeCallable<T : Any>(
    private val name: String,
    private val expectedArgSize: Int,
    private val op: (List<Arg>) -> T
) : DynamikFunction<T> {
    override fun invoke(arguments: List<Arg>, interpreter: TreeWalker, env: Environment): T {
        if (expectedArgSize != arguments.size) {
            throw InvalidArgSize(arguments.size, expectedArgSize, name)
        }
        return op(arguments)
    }
}




