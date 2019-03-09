package scanner

import com.github.h0tk3y.betterParse.lexer.DefaultTokenizer
import com.github.h0tk3y.betterParse.lexer.Token
import com.github.h0tk3y.betterParse.lexer.TokenMatch
import java.lang.RuntimeException


class Scanner {
    fun tokenize(sourceCode: String): List<Tok> {
        return tokenizer.tokenize(sourceCode).filter { !it.type.ignored }.map { wrapToNativeToken(it) }.toList()
    }

    companion object {
        private val tokenizer = buildTokenizer()
        private fun buildTokenizer(): DefaultTokenizer {
            val tokens = mutableListOf<Token>()
            for (tokType in TokenType.values()) {
                tokens.add(
                    Token(
                        tokType.toString(), patternString = tokType.regex.toString(),
                        ignored = tokType == TokenType.WHITESPACE
                    )
                )
            }
            return DefaultTokenizer(tokens)
        }

    }

    /*
    @throws Runt
     */
    fun wrapToNativeToken(tokenMatch: TokenMatch): Tok {
        for (tokType in TokenType.values()) {
            if (tokenMatch.type.name == tokType.toString()) {
                return Tok(tokType, tokenMatch.text)
            }
        }
        throw RuntimeException("${tokenMatch.type} not found")
    }
}


fun main(args: Array<String>) {
    val s = Scanner()
    val toks = s.tokenize("var x = false ")
    toks.forEach { println(it) }
}
