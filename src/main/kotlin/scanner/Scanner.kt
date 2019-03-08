package scanner

import com.github.h0tk3y.betterParse.lexer.DefaultTokenizer
import com.github.h0tk3y.betterParse.lexer.Token
import com.github.h0tk3y.betterParse.lexer.TokenMatch


class Scanner {
    private val tokenizer = buildTokenizer()

    fun tokenize(sourceCode: String): List<TokenMatch> {
        return tokenizer.tokenize(sourceCode).filter { !it.type.ignored }.toList()
    }

    fun buildTokenizer(): DefaultTokenizer {
        val id = Token("id", "\\w+")
        val comma = Token("comma", ",")
        val ws = Token("whitespace", "\\s+", ignored = true)
        val lpar = Token("lpar", "\\(")
        val rpar = Token("rpar", "\\)")
        val print = Token("print", "print")
        val trueToken = Token("true", "true")
        val falseToken = Token("false", "false")
        val functionToken = Token("fn", patternString = "fn")
        val curlyLeft = Token("curlyLeft", patternString = "\\{")
        val curlyRight = Token("curlyLeft", patternString = "}")
        val num = Token("number", "-?\\d+")
        val semicolon = Token("semicolon", ";")

        return DefaultTokenizer(
            listOf(
                ws, lpar, rpar, print, trueToken, falseToken, functionToken
                , curlyLeft, curlyRight, num, id, comma, semicolon
            )
        )
    }
}


fun main(args: Array<String>) {
    val s = Scanner()
    val toks = s.tokenize("fn stuff() { print hello world; }")
    toks.forEach { println(it) }
}
