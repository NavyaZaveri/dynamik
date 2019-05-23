package com.github.navyazaveri.dynamik.stdlib.containers

import com.github.navyazaveri.dynamik.expressions.Arg
import com.github.navyazaveri.dynamik.expressions.DynamikClass
import com.github.navyazaveri.dynamik.interpreter.Environment
import com.github.navyazaveri.dynamik.interpreter.TreeWalker
import com.github.navyazaveri.dynamik.stdlib.NativeCallable
import java.util.stream.Stream
import kotlin.streams.toList


interface Builtin

interface Concatable<T> {
    fun concat(other: T): T
    fun inner(): T
}


class DynamikList : DynamikClass<ListInstance> {
    override fun invoke(arguments: List<Arg>, interpreter: TreeWalker, env: Environment): ListInstance {
        return ListInstance(arguments)
    }
}


class ListInstance(elements: List<Any> = listOf()) : ContainerInstance(), Concatable<ListInstance> {

    override fun concat(other: ListInstance): ListInstance {
        val concat = Stream.concat(this._list.stream(), other._list.stream()).toList()
        return ListInstance(concat)
    }

    override fun inner(): ListInstance {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    override fun toHash(): Int {
        return _list.hashCode()
    }

    override fun hashCode(): Int {
        return _list.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        return (other is ListInstance) && this._list == other._list
    }

    override fun toString(): String {
        return _list.toString()
    }

    private val _list = mutableListOf<Any>()

    init {
        _list.addAll(elements)
        env.defineFunction("add", NativeCallable("list.add", 1) { _list.add(it[0]) })
        env.defineFunction("get", NativeCallable("list.get", 1) { _list[(it[0] as Double).toInt()] })
        env.defineFunction("contains", NativeCallable("list.contains", 1) { _list.contains(it[0]) })
        env.defineFunction("len", NativeCallable("list.len", 0) { _list.size.toDouble() })
    }

    fun contains(thing: Any): Boolean {
        return _list.contains(thing)
    }


}


