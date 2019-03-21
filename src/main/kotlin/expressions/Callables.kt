package expressions

import interpreter.Environment
import interpreter.TreeWalker


typealias FuncName = String
typealias Arg = Any


interface Callable {
    fun invoke(arguments: List<Any>, interpreter: TreeWalker): Any
}


class DynamikCallable(val closure: MutableMap<String, Variable>, val func: FnStmt) : Callable {
    override fun invoke(arguments: List<Any>, interpreter: TreeWalker): Any {
        val env = Environment(closure)

        //set up arguments
        func.params.zip(arguments)
            .forEach { (param, arg) -> env.define(param.lexeme, arg, status = VariableStatus.VAL) }

        //now evaluate all statements against the function environment
        interpreter.evaluateStmts(func.body, env = env)
        return Any()
    }
}

class MemoizedCallable(val closure: MutableMap<String, Variable>, val func: FnStmt) : Callable {
    val env = Environment(closure)

    override fun invoke(arguments: List<Arg>, interpreter: TreeWalker): Any {
        if (cache.contains(Pair(func.functionName.lexeme, arguments))) {
            return cache[Pair(func.functionName.lexeme, arguments)]!!
        }

        //set up arguments
        func.params.zip(arguments)
            .forEach { (param, arg) -> env.define(param.lexeme, arg, status = VariableStatus.VAL) }

        //now evaluate all statements against the function environment
        interpreter.evaluateStmts(func.body, env = env)
        return Any()
    }

    companion object {
        val cache = mutableMapOf<Pair<FuncName, List<Arg>>, Any>()
    }

    fun previouslyExecuted(args: List<Arg>, name: String): Boolean {
        return cache.contains(Pair(name, args))
    }
}

