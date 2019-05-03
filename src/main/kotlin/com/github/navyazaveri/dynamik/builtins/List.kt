package com.github.navyazaveri.dynamik.builtins

import com.github.navyazaveri.dynamik.expressions.Arg
import com.github.navyazaveri.dynamik.expressions.Callable
import com.github.navyazaveri.dynamik.interpreter.Environment
import com.github.navyazaveri.dynamik.interpreter.TreeWalker
import kotlin.collections.List

class List : Callable {
    override fun invoke(arguments: List<Arg>, interpreter: TreeWalker, env: Environment): Any {
        return ListInstance(mutableListOf());
    }
}


//init mapping fuction name to builtin callable. The invoke on this callable
//should basically execute the function. So, perhaps store an instance
// of the list WITHIN the callble (as constructor args) and the function name.
//then just use reflection or something I guess to execute the fuction
//against the consumed instance. Perhaps define method like run method(self, string_method)
//that matches agianst the required method and actually execute the instance

class ListInstance(b: MutableList<Any>) : MutableList<Any> by b {

}

fun main(args: Array<String>) {
    val m = ListInstance(mutableListOf());
    m.add(20);
    m.get(0)
}
