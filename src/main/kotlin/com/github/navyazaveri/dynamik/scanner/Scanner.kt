package com.github.navyazaveri.dynamik.scanner

import com.github.h0tk3y.betterParse.lexer.DefaultTokenizer
import com.github.h0tk3y.betterParse.lexer.Token
import com.github.h0tk3y.betterParse.lexer.TokenMatch
import com.github.navyazaveri.dynamik.errors.InvalidToken

class Scanner {
    private val stringToTokType = TokenType.values().map { Pair(it.toString(), it) }.toMap()
    private val tokenizer = buildTokenizer()

    fun tokenize(sourceCode: String): List<Tok> =
        tokenizer.tokenize(sourceCode).filter { !it.type.ignored }.map { toNativeToken(it) }.toList()

    private fun buildTokenizer(): DefaultTokenizer {
        val tokens = TokenType.values().map {
            Token(it.toString(), patternString = it.regex.toString(), ignored = it == TokenType.WHITESPACE)
        }
        return DefaultTokenizer(tokens)
    }


    private fun convert(match: TokenMatch, type: TokenType): Tok {
        return when (type) {
            TokenType.NUMBER -> Tok(type, match.text, match.text.toDouble(), match.row)
            TokenType.STRING -> Tok(type, match.text, match.text.substring(1, match.text.length - 1), match.row)
            TokenType.TRUE -> Tok(type, match.text, true)
            TokenType.False -> Tok(type, match.text, false, match.row)
            else -> Tok(type, match.text, match.text, match.row)
        }
    }

    private fun toNativeToken(tokenMatch: TokenMatch): Tok {
        if (tokenMatch.type.name in stringToTokType) {
            return convert(tokenMatch, stringToTokType[tokenMatch.type.name]!!)
        }
        throw InvalidToken("${tokenMatch.text} is not a valid token at line ${tokenMatch.row}")
    }
}

fun String.tokenize(): List<Tok> {
    return Scanner().tokenize(this)
}
