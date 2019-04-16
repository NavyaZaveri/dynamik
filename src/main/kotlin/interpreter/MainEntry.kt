package interpreter

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.output.TermUi.echo
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option

class Hello : CliktCommand() {
    val repl by option().flag()
    override fun run() {
        if (repl) {
            Repl.run()
        }
    }
}

fun main(args: Array<String>) {
    Hello().main(args)
}