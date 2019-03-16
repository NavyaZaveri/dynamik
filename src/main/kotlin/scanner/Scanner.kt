package scanner

import com.github.h0tk3y.betterParse.lexer.DefaultTokenizer
import com.github.h0tk3y.betterParse.lexer.Token
import com.github.h0tk3y.betterParse.lexer.TokenMatch
import java.lang.RuntimeException


class Scanner {
    fun tokenize(sourceCode: String): List<Tok> =
        tokenizer.tokenize(sourceCode).filter { !it.type.ignored }.map { wrapToNativeToken(it) }.toList()

    companion object {
        private val tokenizer = buildTokenizer()
        private fun buildTokenizer(): DefaultTokenizer {
            val tokens = mutableListOf<Token>()
            for (nativeTokType in TokenType.values()) {
                tokens.add(
                    Token(
                        nativeTokType.toString(), patternString = nativeTokType.regex.toString(),
                        ignored = nativeTokType == TokenType.WHITESPACE
                    )
                )
            }
            return DefaultTokenizer(tokens)
        }
    }
    
    private fun wrapToNativeToken(tokenMatch: TokenMatch): Tok {
        for (nativeTokType in TokenType.values()) {
            if (tokenMatch.type.name == nativeTokType.toString()) {
                return when (nativeTokType) {
                    TokenType.NUMBER -> Tok(nativeTokType, tokenMatch.text, tokenMatch.text.toDouble(), tokenMatch.row)
                    TokenType.STRING -> Tok(
                        nativeTokType,
                        tokenMatch.text,
                        tokenMatch.text.substring(1, tokenMatch.text.length - 1),
                        tokenMatch.row
                    )
                    TokenType.TRUE -> Tok(nativeTokType, tokenMatch.text, true)
                    TokenType.False -> Tok(nativeTokType, tokenMatch.text, false)
                    else -> Tok(nativeTokType, tokenMatch.text, tokenMatch.text, tokenMatch.row)
                }
            }
        }
        throw RuntimeException("${tokenMatch.text} is not a valid token")
    }
}

fun String.tokenize(): List<Tok> {
    return Scanner().tokenize(this)
}


fun main(args: Array<String>) {
    val s = Scanner()
    val toks = s.tokenize("X==\"hello\" ")
    toks.forEach { println(it) }
}
