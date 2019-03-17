package scanner

/*
A scanner.Token is block
 */

data class Tok(val type: TokenType, val lexeme: String, var literal: Any, val line: Int = 0)

//The following tokentypes are ordered by precedence.

enum class TokenType(val regex: Regex) {

    LEFT_PAREN("\\(".toRegex()),
    RIGHT_PAREN("\\)".toRegex()), LEFT_BRACE("\\{".toRegex()), RIGHT_BRACE("}".toRegex()),
    COMMA(",".toRegex()), DOT("\\.".toRegex()), MINUS("-".toRegex()), PLUS("\\+".toRegex()), SEMICOLON(";".toRegex()), SLASH(
        "/".toRegex()
    ),
    NUMBER("\\d+".toRegex()),

    STAR("\\*".toRegex()),
    QUESTION("\\?".toRegex()),
    WHITESPACE("\\s+".toRegex()),

    BANG_EQUAL("!=".toRegex()),
    BANG("!".toRegex()),

    EQUAL_EQUAL("==".toRegex()),
    EQUAL("=".toRegex()),
    LESS_EQUAL("<=".toRegex()),
    GREATER_EQUAL(">=".toRegex()),

    GREATER(">".toRegex()),
    LESS("<".toRegex()),


    COLON(";".toRegex()),


    STRING("\".*?\"".toRegex()),

    AND("&".toRegex()),
    AND_AND("&".toRegex()),

    CLASS("class".toRegex()), ELSE("else".toRegex()),
    FN("fn".toRegex()),
    FOR("for".toRegex()),
    IF("if".toRegex()),
    NIL("nil".toRegex()),
    TRUE("true".toRegex()),
    False("false".toRegex()),
    VAR("var".toRegex()),

    PRINT("print".toRegex()), RETURN("return".toRegex()),
    LOOP("loop".toRegex()),
    VAL("val".toRegex()),
    Par("par".toRegex()),
    While("while".toRegex()),

    IDENTIFIER("\\w+".toRegex()),
    EOF("[\r\n]+".toRegex()),
}
