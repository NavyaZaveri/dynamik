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
        val mul = Token("mul", patternString = "\\*")
        val add = Token("mul", patternString = "\\+")
        val minus = Token("minus", patternString = "-")
        val div = Token("div", patternString = "/")
        val bang_bang = Token("bang_bang", patternString = "!!")
        val bang = Token("bang", patternString = "!")
        val bang_equals = Token("bang_equal", patternString = "!=")
        val equal_equals = Token("equal_equals", patternString = "==")
        val STRINGLIT = Token(name = "literal", patternString = "\".*?\"")





        return DefaultTokenizer(
            listOf(
                ws, lpar, rpar, print, trueToken, falseToken, functionToken
                , curlyLeft, curlyRight, num, id, comma, semicolon,
                mul, add, minus, div, bang, bang_bang, bang_equals, equal_equals,
                STRINGLIT
            )
        )
    }
}


fun main(args: Array<String>) {
    val s = Scanner()
    val toks = s.tokenize("fn stuff() { print \"hello\" world 3+5 5==3; }")
    toks.forEach { println(it) }
}
