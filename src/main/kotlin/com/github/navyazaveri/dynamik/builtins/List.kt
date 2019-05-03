package com.github.navyazaveri.dynamik.builtins

import com.github.navyazaveri.dynamik.expressions.Arg
import com.github.navyazaveri.dynamik.expressions.Callable
import com.github.navyazaveri.dynamik.interpreter.Environment
import com.github.navyazaveri.dynamik.interpreter.TreeWalker
import kotlin.collections.List


interface DynInstance {
    fun runMethod(d: DynInstance, methodName: String, args: List<Arg>): Any
}

interface Builtin


class List : Callable {
    override fun invoke(arguments: List<Arg>, interpreter: TreeWalker, env: Environment): Any {
        return ListInstance();
    }
}


//init mapping fuction name to builtin callable. The invoke on this callable
//should basically execute the function. So, perhaps store an instance
// of the list WITHIN the callble (as constructor args) and the function name.
//then just use reflection or something I guess to execute the fuction
//against the consumed instance. Perhaps define method like run method(self, string_method)
//that matches agianst the required method and actually execute the instance


//deleate maybe? reflection should be able to pick uo on delegated
//methods
class ListInstance : Builtin {

}

class BuiltinCallable(val b: Builtin, val methodName: String) : Callable {
    override fun invoke(arguments: List<Arg>, interpreter: TreeWalker, env: Environment): Any {
        TODO()
    }

    fun runMethod(): Any {
        return b::class.java.getMethod(methodName).invoke(b)
    }
}