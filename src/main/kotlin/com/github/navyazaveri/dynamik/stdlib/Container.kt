package com.github.navyazaveri.dynamik.stdlib

import com.github.navyazaveri.dynamik.expressions.BuiltinCallable
import com.github.navyazaveri.dynamik.expressions.DynamikInstance


interface Container : Builtin {
    override fun toString(): String
    fun toHash(): Int
}

abstract class DynmaikContainer<T : Collection<Any>> : DynamikInstance() {
    abstract val nativeContainer: T

    init {
        env.defineFunction("size", NativeCallable("size", 0) { nativeContainer.size })
        env.defineFunction("toString", NativeCallable("toString", 0) { nativeContainer.toString() })
        env.defineFunction("toHash", NativeCallable("toHash", 0) { nativeContainer.hashCode() })
    }
}

/**
 * All containers inheriting this instance must defined their
 * functions in the instance env, with a BuiltinCallable.
 * @code  env.defineFunction("len", BuiltinCallable(this, "len", 0))
 *
 */
abstract class ContainerInstance : Container, DynamikInstance() {
    init {
        env.defineFunction("toString", BuiltinCallable(this, "toString", 0))
        env.defineFunction("toHash", BuiltinCallable(this, "toHash", 0))
    }
}
