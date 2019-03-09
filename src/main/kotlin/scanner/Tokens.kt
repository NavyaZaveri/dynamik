package scanner

/*
A scanner.Token is block
 */

data class Tok(val type: TokenType, var literal: Any, val line: Int = 0) {
    init {
        when (type) {
            TokenType.STRING -> literal = literal as String
            TokenType.NUMBER -> literal = literal as Double
        }
    }
}

//All tokenetypes are ordered by precedence

enum class TokenType(val regex: Regex) {

    // Single-character tokens.
    LEFT_PAREN("\\(".toRegex()),
    RIGHT_PAREN("\\)".toRegex()), LEFT_BRACE("\\{".toRegex()), RIGHT_BRACE("}".toRegex()),
    COMMA(",".toRegex()), DOT("\\.".toRegex()), MINUS("-".toRegex()), PLUS("\\+".toRegex()), SEMICOLON(";".toRegex()), SLASH(
        "/".toRegex()
    ),
    STAR("\\*".toRegex()),
    QUESTION("\\?".toRegex()),
    WHITESPACE("\\s+".toRegex()),

    // One or two character tokens.
    BANG("!".toRegex()),
    BANG_EQUAL("!=".toRegex()),
    EQUAL("=".toRegex()), EQUAL_EQUAL("==".toRegex()),
    GREATER(">".toRegex()), GREATER_EQUAL(">=".toRegex()),
    LESS("<".toRegex()), LESS_EQUAL("<=".toRegex()), COLON(";".toRegex()),


    // Literals.
    STRING("\".*?\"".toRegex()),
    NUMBER("-?\\d+".toRegex()),

    // Keywords.
    AND("&&".toRegex()),
    CLASS("class".toRegex()), ELSE("else".toRegex()), FALSE("false".toRegex()), FN("fn".toRegex()), FOR("for".toRegex()), IF(
        "if".toRegex()
    ),

    NIL("nil".toRegex()),
    TRUE("true".toRegex()),
    False("false".toRegex()),
    VAR("var".toRegex()),
    PRINT("print".toRegex()), RETURN("return".toRegex()),
    LOOP("loop".toRegex()),

    EOF("[\r\n]+".toRegex()),
    IDENTIFIER("\\w+".toRegex()),
}

fun main(args: Array<String>) {
    TokenType.LEFT_PAREN.also { println(it) }
}