package com.github.navyazaveri.dynamik.expressions

import com.github.navyazaveri.dynamik.errors.InvalidArgSize
import com.github.navyazaveri.dynamik.interpreter.DynamikClass
import com.github.navyazaveri.dynamik.interpreter.Environment
import com.github.navyazaveri.dynamik.interpreter.TreeWalker

typealias FuncName = String
typealias Arg = Any
typealias RetVal = Any


interface Callable<T : Any> {
    fun invoke(arguments: List<Arg>, interpreter: TreeWalker, env: Environment = Environment()): T
}

class DynamikCallable(val func: FnStmt) : Callable<Any> {

    override fun invoke(args: List<Arg>, interpreter: TreeWalker, env: Environment): Any {

        // check args size
        if (args.size != func.params.size) {
            throw InvalidArgSize(expected = func.params.size, actual = args.size, fname = func.functionName.lexeme)
        }

        val outer = interpreter.env

        // set up args
        func.params.zip(args)
            .forEach { (param, arg) ->
                env.define(param.lexeme, arg, status = VariableStatus.VAR)
            }


        //make functions visible
        outer.functions()
            .forEach { (k, v) -> env.defineFunction(k, v.value) }

        //if it's a class environment, then it will have some associated fields. Make these visible.
        outer.fields()
            .forEach { (k, v) -> env.define(k, v.value, VariableStatus.VAR) }


        //makes  classes in out scope visible to current env
        outer.classes().forEach { k, u -> env.defineClass(k, u.value) }


        // now evaluate all statements against the environment supplied to the function
        try {
            interpreter.evaluateStmts(func.body, env = env)
        } catch (r: Return) {
            return r.value
            // update fields of the original environment and globals (todo)
        } finally {

            for ((k, v) in env.identifierToValue) {
                if (k in outer.fields) {
                    outer.fields[k] = v
                    outer.identifierToValue[k] = v
                }
            }
        }
        return Any()
    }

}


class MemoizedCallable(val func: FnStmt) : Callable<Any> {
    var hits = 0

    companion object {
        val cache = mutableMapOf<Pair<FuncName, List<Arg>>, Any>()
    }

    val defaultCallable by lazy { DynamikCallable(func) }

    /**
     * Invokes the callable if its result has not already been cached. Otherwise, the cached value
     * is returned
     */
    override fun invoke(arguments: List<Arg>, interpreter: TreeWalker, env: Environment): Any {
        val funcKey = Pair(func.functionName.lexeme, arguments)

        if (cache.contains(funcKey)) {
            return cache[funcKey]!!.also { /* println("cache hit!"); */ hits += 1 }
        }
        return defaultCallable.invoke(arguments, interpreter).also { cache[funcKey] = it }
    }
}

