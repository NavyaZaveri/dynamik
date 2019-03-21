package expressions

import interpreter.Environment
import interpreter.TreeWalker


typealias FuncName = String
typealias Arg = Any


interface Callable {
    fun invoke(arguments: List<Any>, interpreter: TreeWalker, env: Environment = Environment.new()): Any
}


class DynamikCallable(val func: FnStmt) : Callable {

    override fun invoke(
        arguments: List<Any>,
        interpreter: TreeWalker,
        env: Environment
    ): Any {

        //set up arguments
        func.params.zip(arguments)
            .forEach { (param, arg) -> env.define(param.lexeme, arg, status = VariableStatus.VAL) }

        //now evaluate all statements against the function environment
        interpreter.evaluateStmts(func.body, env = env)
        return Any()
    }
}

class MemoizedCallable(val func: FnStmt) : Callable {
    val defaultCallable = DynamikCallable(func)

    override fun invoke(arguments: List<Arg>, interpreter: TreeWalker, env: Environment): Any {
        val funcKey = Pair(func.functionName.lexeme, arguments)
        if (cache.contains(funcKey)) {
            return cache[funcKey]!!
        }
        return defaultCallable.invoke(arguments, interpreter).also { cache[funcKey] = it }
    }

    companion object {
        val cache = mutableMapOf<Pair<FuncName, List<Arg>>, Any>()
    }

    fun previouslyExecuted(args: List<Arg>, name: String): Boolean {
        return cache.contains(Pair(name, args))
    }
}

