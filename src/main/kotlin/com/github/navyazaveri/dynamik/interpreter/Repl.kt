package com.github.navyazaveri.dynamik.interpreter

import com.github.navyazaveri.dynamik.expressions.Stmt
import com.github.navyazaveri.dynamik.parser.parseStmts
import com.github.navyazaveri.dynamik.scanner.tokenize


class Repl {
    val interpreter = TreeWalker()

    fun eval(statememt: Stmt): Any {
        return statememt.evaluateBy(interpreter)
    }

    fun eval(sourceCode: String): Any {
        return this.eval(sourceCode.tokenize().parseStmts())
    }

    fun eval(statements: List<Stmt>): Any {
        return statements.map { eval(it) }.last()
    }

    fun clear() {
        interpreter.clear()
    }

    companion object {
        @JvmStatic
        fun run() {
            val repl = Repl()
            while (true) {
                print(">>")
                try {
                    readLine()!!.also { println(repl.eval(it)) }
                } catch (r: Exception) {
                    println(r.message)
                }
            }
        }
    }
}
