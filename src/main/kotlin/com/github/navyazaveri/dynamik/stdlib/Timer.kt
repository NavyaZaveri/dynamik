package com.github.navyazaveri.dynamik.stdlib

import com.github.navyazaveri.dynamik.expressions.Arg
import com.github.navyazaveri.dynamik.expressions.DynamikClass
import com.github.navyazaveri.dynamik.expressions.DynamikInstance
import com.github.navyazaveri.dynamik.interpreter.Environment
import com.github.navyazaveri.dynamik.interpreter.TreeWalker

class TimerClass : DynamikClass<TimerInstance> {
    override fun invoke(arguments: List<Arg>, interpreter: TreeWalker, env: Environment): TimerInstance {
        return TimerInstance()
    }
}

class TimerInstance : DynamikInstance() {
    override fun toHash(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun toString(): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}