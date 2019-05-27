package com.github.navyazaveri.dynamik.scanner

import com.github.h0tk3y.betterParse.lexer.DefaultTokenizer
import com.github.h0tk3y.betterParse.lexer.Token
import com.github.h0tk3y.betterParse.lexer.TokenMatch
import com.github.navyazaveri.dynamik.errors.InvalidToken

class Scanner {
    fun tokenize(sourceCode: String): List<Tok> =
        tokenizer.tokenize(sourceCode).filter { !it.type.ignored }.map { wrapToNativeToken(it) }.toList()

    companion object {
        private val tokenizer = buildTokenizer()
        private fun buildTokenizer(): DefaultTokenizer {
            val tokens = TokenType.values().map {
                Token(it.toString(), patternString = it.regex.toString(), ignored = it == TokenType.WHITESPACE)
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
                    TokenType.False -> Tok(nativeTokType, tokenMatch.text, false, tokenMatch.row)
                    else -> Tok(nativeTokType, tokenMatch.text, tokenMatch.text, tokenMatch.row)
                }
            }
        }
        throw InvalidToken("${tokenMatch.text} is not a valid token at line ${tokenMatch.row}")
    }
}

fun String.tokenize(): List<Tok> {
    return Scanner().tokenize(this)
}
