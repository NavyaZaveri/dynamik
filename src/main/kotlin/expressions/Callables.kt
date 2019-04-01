package expressions

import interpreter.Environment
import interpreter.TreeWalker
import java.lang.RuntimeException

typealias FuncName = String
typealias Arg = Any

interface Callable {
    fun invoke(arguments: List<Any>, interpreter: TreeWalker, env: Environment = Environment()): Any
}

class DynamikCallable(val func: FnStmt) : Callable {

    override fun invoke(args: List<Any>, interpreter: TreeWalker, env: Environment): Any {

        if (args.size != func.params.size) {
            throw RuntimeException("${func.functionName.lexeme} takes ${func.params.size} args, supplied ${args.size}.")
        }

        // set up args
        func.params.zip(args)
            .forEach { (param, arg) ->
                env.define(param.lexeme, arg, status = VariableStatus.VAR)

                // functions are global, put them into the local environment
                interpreter.env.globals()
                    .forEach { (k, v) -> env.define(k, v.value, VariableStatus.VAL) }

                // now evaluate all statements against the function environment
                try {
                    interpreter.evaluateStmts(func.body, env = env)
                } catch (r: Return) {
                    return r.value
                }
            }
        return Any()
    }
}

class MemoizedCallable(val func: FnStmt) : Callable {

    companion object {
        val cache = mutableMapOf<Pair<FuncName, List<Arg>>, Any>()
    }

    val defaultCallable by lazy { DynamikCallable(func) }

    override fun invoke(arguments: List<Arg>, interpreter: TreeWalker, env: Environment): Any {
        val funcKey = Pair(func.functionName.lexeme, arguments)
        if (cache.contains(funcKey)) {
            return cache[funcKey]!!.also { println("cache hit!") }
        }
        return defaultCallable.invoke(arguments, interpreter).also { cache[funcKey] = it }
    }
}
