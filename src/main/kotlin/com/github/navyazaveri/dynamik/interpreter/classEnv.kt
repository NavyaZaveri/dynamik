package com.github.navyazaveri.dynamik.interpreter

import com.github.navyazaveri.dynamik.expressions.Callable
import com.github.navyazaveri.dynamik.expressions.Variable


class ClassEnv {
    val fields = mutableMapOf<String, Variable>()
    val methods = mutableSetOf<String>()
}