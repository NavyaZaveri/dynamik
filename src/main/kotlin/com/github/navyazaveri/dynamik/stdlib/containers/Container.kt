package com.github.navyazaveri.dynamik.stdlib.containers

import com.github.navyazaveri.dynamik.expressions.DynamikInstance
import com.github.navyazaveri.dynamik.stdlib.NativeCallable

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

}
