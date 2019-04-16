package interpreter

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.output.TermUi.echo
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.file
import parser.parseExpr
import parser.parseStmts
import scanner.tokenize
import java.io.File

class CommandLineParser : CliktCommand() {
    val repl by option().flag()
    val file by option("-f", "--file", help = "file to exectu")

    override fun run() {
        if (repl) {
            Repl.run()
        } else {
            if (file != null) {
                val stuff = File(file).readText()
                stuff.tokenize().parseStmts().evaluateAllBy(TreeWalker()).also { println(it) }
            }
        }

    }
}

fun main(args: Array<String>) {
    CommandLineParser().main(args)
}