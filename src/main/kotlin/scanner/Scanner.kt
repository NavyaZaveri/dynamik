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
                return Tok(tokType, tokenMatch.text, tokenMatch.row)
            }
        }
        throw RuntimeException("${tokenMatch.text} is not a valid token")
    }
}


fun main(args: Array<String>) {
    val s = Scanner()
    val toks = s.tokenize("X==5")
    toks.forEach { println(it) }
}
