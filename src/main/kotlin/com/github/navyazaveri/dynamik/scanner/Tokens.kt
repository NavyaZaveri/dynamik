package com.github.navyazaveri.dynamik.scanner

data class Tok(val type: TokenType, val lexeme: String, var literal: Any, val line: Int = 0)

enum class TokenType(val regex: Regex) {


    // Separators
    LEFT_PAREN("\\(".toRegex()),
    RIGHT_PAREN("\\)".toRegex()), LEFT_BRACE("\\{".toRegex()), RIGHT_BRACE("}".toRegex()),
    COMMA(",".toRegex()),
    SEMICOLON(";".toRegex()), SLASH("/".toRegex()),


    NUMBER("\\d+".toRegex()),

    WHITESPACE("\\s+".toRegex()),

    // Operators
    BANG_EQUAL("!=".toRegex()),
    BANG("!".toRegex()),
    EQUAL_EQUAL("==".toRegex()),
    EQUAL("=".toRegex()),
    LESS_EQUAL("<=".toRegex()),
    GREATER_EQUAL(">=".toRegex()),
    STAR("\\*".toRegex()),
    DOT("\\.".toRegex()),
    PLUS("\\+".toRegex()),
    MINUS("-".toRegex()),
    GREATER(">".toRegex()),
    LESS("<".toRegex()),
    AND_AND("&&".toRegex()),

    STRING("\".*?\"".toRegex()),


    NIL("nil".toRegex()),
    TRUE("true".toRegex()),
    False("false".toRegex()),

    // Keywords

    GLOBAL("@global".toRegex()),
    PRINT("""\bprint\b""".toRegex()),
    RETURN("""\breturn\b""".toRegex()),
    VAL("""\bval\b""".toRegex()),
    VAR("""\bvar\b""".toRegex()),
    Par("@par".toRegex()),
    While("""\bwhile\b""".toRegex()),
    Memo("@memo".toRegex()),
    Wait("@wait".toRegex()),
    ASSERT("assert".toRegex()),
    CLASS("class".toRegex()), ELSE("else".toRegex()),
    FN("""\bfn\b""".toRegex()),
    FOR("for".toRegex()),
    IF("if".toRegex()),

    PAR_WITH_LOCK("@par_lock".toRegex()),


    IDENTIFIER("\\w+".toRegex()),

    EOF("[\r\n]+".toRegex()),

}
