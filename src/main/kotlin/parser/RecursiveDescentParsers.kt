package parser

import interpreter.Rpn
import scanner.Scanner
import scanner.Tok
import scanner.TokenType
import java.lang.RuntimeException

/*
expression     → assignment ;

assignment     → ( call "." )? IDENTIFIER "=" assignment
| logic_or;

logic_or       → logic_and ( "or" logic_and )* ;
logic_and      → equality ( "and" equality )* ;
equality       → comparison ( ( "!=" | "==" ) comparison )* ;
comparison     → addition ( ( ">" | ">=" | "<" | "<=" ) addition )* ;
addition       → multiplication ( ( "-" | "+" ) multiplication )* ;
multiplication → unary ( ( "/" | "*" ) unary )* ;

unary          → ( "!" | "-" ) unary | call ;
call           → primary ( "(" arguments? ")" | "." IDENTIFIER )* ;
primary        → "true" | "false" | "nil" | "this"
| NUMBER | STRING | IDENTIFIER | "(" expression ")"
| "super" "." IDENTIFIER ;*/


class ExprParser(val tokens: List<Tok>) {
    var current = 0

    fun parse(): Expr {
        return addition()
    }


    //matches against tbe single token and then advances
    fun primary(): Expr {
        return when (tokens[current].type) {
            TokenType.NUMBER -> LiteralExpr(token = tokens[current])
            TokenType.STRING -> LiteralExpr(token = tokens[current])
            TokenType.IDENTIFIER -> LiteralExpr(token = tokens[current])
            else -> throw RuntimeException("could not recognize ${tokens[current]}")
        }.also { advance() }
    }

    fun consume(): Tok = tokens[current].also { current += 1 }

    fun advance() {
        current += 1
    }

    fun match(vararg tokenTypes: TokenType): Boolean =
        tokenTypes.any { current < tokens.size && it == tokens[current].type }

    fun addition(): Expr {
        var expr = multiplication()
        while (match(TokenType.PLUS, TokenType.MINUS)) {
            val operator = consume()
            val right = multiplication()
            expr = BinaryExpr(expr, operator, right)
        }
        return expr

    }

    fun multiplication(): Expr {
        var expr = unary()
        while (match(TokenType.STAR, TokenType.SLASH)) {
            val operator = consume()
            val right = unary()
            expr = BinaryExpr(expr, operator, right)
        }
        return expr
    }

    fun unary(): Expr {
        val expr = primary()
        if (match(TokenType.BANG)) {
            return UnaryExpr(consume(), expr)
        }
        return expr
    }
}

fun main(args: Array<String>) {
    val toks = Scanner().tokenize("20+30 * 50")
    ExprParser(toks).parse().also { Rpn().prettyPrint(it).also { println(it) } }
}