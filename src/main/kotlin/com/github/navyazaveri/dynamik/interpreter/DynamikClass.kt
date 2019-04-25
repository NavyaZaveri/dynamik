package com.github.navyazaveri.dynamik.interpreter

import com.github.navyazaveri.dynamik.expressions.Arg
import com.github.navyazaveri.dynamik.expressions.Callable
import com.github.navyazaveri.dynamik.expressions.VariableStatus


typealias Method = String


class DynamikInstance(val env: Environment = Environment()) : Callable {
    override fun invoke(arguments: List<Arg>, interpreter: TreeWalker, env: Environment): Any {
        return this;
    }

    fun defineMethod(methodName: String, block: Callable) {
        env.define(methodName, block, VariableStatus.VAL)
    }

    fun defineField(fieldName: String, value: Any, status: VariableStatus) {
        env.define(fieldName, value, status)
    }

    fun runMethod(methodname: String, args: List<Any>, interpreter: TreeWalker) {
        val block = env.get(methodname) as Callable
        block.invoke(args, interpreter, this.env)
    }
}

fun main(args: Array<String>) {

}