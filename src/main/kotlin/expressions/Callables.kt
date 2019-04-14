package expressions

import errors.InvalidArgumentSize
import interpreter.Environment
import interpreter.TreeWalker
import interpreter.Rpn

typealias FuncName = String
typealias Arg = Any
typealias RetVal = Any

interface Callable {
    fun invoke(arguments: List<Any>, interpreter: TreeWalker, env: Environment = Environment()): Any
}


class DynamikCallable(val func: FnStmt) : Callable {

    override fun invoke(args: List<Arg>, interpreter: TreeWalker, env: Environment): Any {

        if (args.size != func.params.size) {
            throw InvalidArgumentSize("${func.functionName.lexeme} takes ${func.params.size} args, supplied ${args.size}.")
        }


        // set up args
        func.params.zip(args)
            .forEach { (param, arg) ->
                env.define(param.lexeme, arg, status = VariableStatus.VAR)
            }

        // functions are global, put them into the local environment
        interpreter.env.globals()
            .forEach { (k, v) -> env.define(k, v.value, VariableStatus.VAL) }

        // now evaluate all statements against the environment supplied to the function
        try {
            interpreter.evaluateStmts(func.body, env = env)
        } catch (r: Return) {
            return r.value
        }
        return Any()

    }
}

class MemoizedCallable(val func: FnStmt) : Callable {
    var hits = 0

    companion object {
        val cache = mutableMapOf<Pair<FuncName, List<Arg>>, Any>()
    }

    val defaultCallable by lazy { DynamikCallable(func) }

    /**
     * Invokes the callable if its result has not already been cached. Otherwise the cache value
     * value is returned
     */
    override fun invoke(arguments: List<Arg>, interpreter: TreeWalker, env: Environment): Any {
        val funcKey = Pair(func.functionName.lexeme, arguments)

        if (cache.contains(funcKey)) {
            return cache[funcKey]!!.also { println("cache hit!"); hits += 1 }
        }
        return defaultCallable.invoke(arguments, interpreter).also { cache[funcKey] = it }
    }
}
