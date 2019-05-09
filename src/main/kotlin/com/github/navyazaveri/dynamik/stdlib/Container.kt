package com.github.navyazaveri.dynamik.stdlib

import com.github.navyazaveri.dynamik.expressions.DynamikInstance


interface Container : Builtin {
    fun len(): Int
    override fun toString(): String
    fun toHash(): Int
}

/**
 * All containers inheriting this instannce must defined their
 * functions in the instance env, with a BuiltinCallable.
 * @code  env.defineFunction("len", BuiltinCallable(this, "len", 0))
 *
 */
abstract class ContainerInstance : Container, DynamikInstance() {
    init {
        env.defineFunction("len", BuiltinCallable(this, "len", 0))
        env.defineFunction("toString", BuiltinCallable(this, "toString", 0))
        env.defineFunction("toHash", BuiltinCallable(this, "toHash", 0))
    }
}
