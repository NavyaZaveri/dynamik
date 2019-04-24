package com.github.navyazaveri.dynamik.interpreter

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.navyazaveri.dynamik.parser.parseStmts
import com.github.navyazaveri.dynamik.scanner.tokenize
import java.io.File

class CommandLineParser : CliktCommand() {
    val repl by option().flag()
    val file by option("-f", "--file", help = "file to execute")

    override fun run() {
        if (repl) {
            Repl.run()
        } else {
            if (file != null) {
                val code = File(file).readText()
                Repl().eval(code)
            }
        }
    }
}

fun main(args: Array<String>) {
    CommandLineParser().main(args)
}