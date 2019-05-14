package com.github.navyazaveri.dynamik.stdlib

import com.github.navyazaveri.dynamik.expressions.Arg
import com.github.navyazaveri.dynamik.expressions.DynamikClass
import com.github.navyazaveri.dynamik.expressions.DynamikFunction
import com.github.navyazaveri.dynamik.expressions.DynamikInstance
import com.github.navyazaveri.dynamik.interpreter.Environment
import com.github.navyazaveri.dynamik.interpreter.TreeWalker

class Clock : DynamikFunction<Double> {
    override fun invoke(arguments: List<Arg>, interpreter: TreeWalker, env: Environment): Double {
        return System.currentTimeMillis().toDouble()
    }
}