package com.github.navyazaveri.dynamik.stdlib

import com.github.navyazaveri.dynamik.errors.InvalidArgSize
import com.github.navyazaveri.dynamik.expressions.Arg
import com.github.navyazaveri.dynamik.expressions.DynamikFunction
import com.github.navyazaveri.dynamik.interpreter.Environment
import com.github.navyazaveri.dynamik.interpreter.TreeWalker


/**
 * A simple FFI that allows kotlin to power some of Dynamik's builtin
 * functions
 */
class NativeCallable<T : Any>(
    private val name: String,
    private val expectedArgSize: Int,
    private val op: (List<Arg>) -> T
) : DynamikFunction<T> {
    override fun invoke(arguments: List<Arg>, interpreter: TreeWalker, env: Environment): T {
        if (expectedArgSize != arguments.size) {
            throw InvalidArgSize(arguments.size, expectedArgSize, name)
        }
        return op(arguments)
    }
}

