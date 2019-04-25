package com.github.navyazaveri.dynamik.interpreter

import com.github.navyazaveri.dynamik.expressions.Callable
import com.github.navyazaveri.dynamik.expressions.VariableStatus


typealias Method = String

class DynamikInstance {

}

class DynamikClass(val env: Environment = Environment()) {
    init {
        env.define("this", env, status = VariableStatus.VAL)
    }



    fun addMethod(methodName: String, value: Any) {
        env.define(methodName, value, status = VariableStatus.VAL)
    }

    fun addField(fieldName: String, value: Any, status: VariableStatus = VariableStatus.VAL) {
        env.define(fieldName, value, status = status)
    }

    fun invokeMethod(name: String, args: List<Any>, interpreter: TreeWalker): Any {
        val methodname = env.get(name)
        val callable = env.get(name) as Callable
        return callable.invoke(args, interpreter = interpreter, env = this.env)
    }
}

fun main(args: Array<String>) {

}