package com.github.navyazaveri.dynamik.stdlib.containers

import com.github.navyazaveri.dynamik.expressions.DynamikInstance


interface Container : Builtin {
    override fun toString(): String
    fun toHash(): Int
    override fun equals(other: Any?): Boolean
}

abstract class ContainerInstance : Container, DynamikInstance()
