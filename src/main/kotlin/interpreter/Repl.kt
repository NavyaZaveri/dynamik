package interpreter

import expressions.Stmt
import parser.parseStmts
import scanner.tokenize

class Repl {
    val interpreter = TreeWalker()

    fun eval(statememt: Stmt): Any {
        return statememt.evaluateBy(interpreter)
    }

    fun eval(sourceCode: String): Any {
        return this.eval(sourceCode.tokenize().parseStmts())
    }

    fun eval(statements: List<Stmt>): Any {
        val res = statements.map { eval(it) }
        return res[statements.size - 1]
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val repl = Repl()
            while (true) {
                print(">>")
                try {
                    readLine()!!.also { repl.eval(it.tokenize().parseStmts()).also { println(it) } }
                } catch (r: Exception) {
                    println(r.message)
                }
            }
        }
    }
}
