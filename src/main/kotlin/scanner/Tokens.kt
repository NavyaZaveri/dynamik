package scanner/*
A scanner.Token is block
 */

data class Token0(val type: TokenType, val literal: Any, val line: Int)

enum class TokenType {
    // Single-character tokens.
    LEFT_PAREN,
    RIGHT_PAREN, LEFT_BRACE, RIGHT_BRACE,
    COMMA, DOT, MINUS, PLUS, SEMICOLON, SLASH, STAR, QUESTION,

    // One or two character tokens.
    BANG,
    BANG_EQUAL,
    EQUAL, EQUAL_EQUAL,
    GREATER, GREATER_EQUAL,
    LESS, LESS_EQUAL, COLON,

    // Literals.
    IDENTIFIER,
    STRING, NUMBER,

    // Keywords.
    AND,
    CLASS, ELSE, FALSE, FUN, FOR, IF, NIL, OR,
    PRINT, RETURN, SUPER, THIS, TRUE, VAR, WHILE,
    LOOP,

    EOF

}