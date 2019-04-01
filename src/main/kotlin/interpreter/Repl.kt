package interpreter

import expressions.Stmt
import parser.parseStmts
import scanner.tokenize

class Repl {
    val interpreter = TreeWalker()

    fun eval(statememt: Stmt): Any {
        return statememt.evaluateBy(interpreter)
    }

    fun eval(statements: List<Stmt>): Any {
        val res = statements.map { eval(it) }
        return res[statements.size - 1]
    }
}

fun main(args: Array<String>) {
    val r = Repl()
    val stmts = "val x = 3; x+1; x+2;".tokenize().parseStmts()
    r.eval(stmts).also { println(it) }
}