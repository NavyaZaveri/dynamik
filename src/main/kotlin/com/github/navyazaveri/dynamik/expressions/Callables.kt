package com.github.navyazaveri.dynamik.expressions

import com.github.navyazaveri.dynamik.errors.InvalidArgSize
import com.github.navyazaveri.dynamik.interpreter.Environment
import com.github.navyazaveri.dynamik.interpreter.TreeWalker
import com.github.navyazaveri.dynamik.stdlib.Builtin

typealias FuncName = String
typealias Arg = Any
typealias RetVal = Any


/**
 * Types that can be invoked implement [Callable]. Eg: Functions, Classes, class methods
 */
interface Callable<T : Any> {
    fun invoke(arguments: List<Arg>, interpreter: TreeWalker, env: Environment = Environment()): T
}


//marker traits to distinguish between functions and classes

interface DynamikFunction<T : Any> : Callable<T>
interface DynamikClass<T : DynamikInstance> : Callable<T>

abstract class DynamikInstance {
    val env = Environment()
    abstract override fun toString(): String
    abstract fun toHash(): Int
}


class DefaultFunction(val func: FnStmt) : DynamikFunction<Any> {

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

        //if it's a class environment, it will have some associated fields. Make these visible.
        outer.fields()
            .forEach { (k, v) -> env.defineField(k, v.value) }


        //makes all existing classes visible to current env
        outer.classes().forEach { k, u -> env.defineClass(k, u.value) }


        // now evaluate all statements against the environment supplied to the function
        try {
            interpreter.evaluateStmts(func.body, env = env)
        } catch (r: Return) {
            return r.value


        } finally {  // update fields of the original environment and globals (todo)

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

/*
A special type of functions that caches the results of a functions
against its inputs. Should be used ONLY for pure functions. As such,
the language does not allow class methods to be memoizable.
*/
class MemoizedFunction(val func: FnStmt) : DynamikFunction<Any> {
    var hits = 0

    companion object {
        val cache = mutableMapOf<Pair<FuncName, List<Arg>>, Any>()
    }

    val defaultCallable by lazy { DefaultFunction(func) }

    override fun invoke(arguments: List<Arg>, interpreter: TreeWalker, env: Environment): Any {
        val funcKey = Pair(func.functionName.lexeme, arguments)

        if (cache.contains(funcKey)) {
            return cache[funcKey]!!.also { /* println("cache hit!"); */ hits += 1 }
        }
        return defaultCallable.invoke(arguments, interpreter).also { cache[funcKey] = it }
    }
}

class BuiltinCallable(val b: Builtin, val methodName: String, val arity: Int) : DynamikFunction<Any> {


    /**
     * Uses reflection to invoke the builtin method.*/
    override fun invoke(arguments: List<Arg>, interpreter: TreeWalker, env: Environment): Any {
        val argTypes = (0 until arity).map { Any::class.java }.toTypedArray()
        return b::class.java.getMethod(methodName, *argTypes).invoke(b, *arguments.toTypedArray())
    }
}

