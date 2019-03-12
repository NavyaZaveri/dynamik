package parser

import expressions.BinaryExpr
import expressions.Expr
import expressions.LiteralExpr
import expressions.UnaryExpr
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
    private var current = 0

    fun printStmt() {

    }

    fun AssignStmt() {

    }

    fun variableStmt() {

    }

    fun parse(): Expr {
        return equality()
    }

    fun equality(): Expr {
        var expr = comparison()
        while (match(TokenType.EQUAL_EQUAL, TokenType.BANG_EQUAL)) {
            val operand = advance()
            val right = comparison()
            expr = BinaryExpr(expr, operand, right)
        }
        return expr
    }

    fun comparison(): Expr {
        var expr = addition()
        while (match(TokenType.LESS_EQUAL, TokenType.GREATER_EQUAL, TokenType.LESS, TokenType.GREATER)) {
            val operand = advance()
            val right = comparison()
            expr = BinaryExpr(expr, operand, right)
        }
        return expr
    }


    //matches against tbe single token and then advances
    private fun primary(): Expr {
        return when (tokens[current].type) {
            TokenType.NUMBER -> LiteralExpr(token = tokens[current])
            TokenType.STRING -> LiteralExpr(token = tokens[current])
            TokenType.IDENTIFIER -> LiteralExpr(token = tokens[current])
            TokenType.TRUE -> LiteralExpr(token = tokens[current])
            TokenType.False -> LiteralExpr(token = tokens[current])
            else -> throw RuntimeException("could not recognize ${tokens[current]}")
        }.also { advance() }
    }

    private fun advance(): Tok = tokens[current].also { current += 1 }

    fun consume(tokType: TokenType) {
        if (tokens[current].type != tokType) {
            throw RuntimeException("expecting $tokType, found ${tokens[current].type} instead")
        }
        current += 1
    }


    fun allTokensConsumed(): Boolean = current >= tokens.size

    fun match(vararg tokenTypes: TokenType): Boolean =
        tokenTypes.any { !allTokensConsumed() && it == tokens[current].type }

    private fun addition(): Expr {
        var expr = multiplication()
        while (match(TokenType.PLUS, TokenType.MINUS)) {
            val operator = advance()
            val right = multiplication()
            expr = BinaryExpr(expr, operator, right)
        }
        return expr

    }

    fun multiplication(): Expr {
        var expr = unary()
        while (match(TokenType.STAR, TokenType.SLASH)) {
            val operator = advance()
            val right = unary()
            expr = BinaryExpr(expr, operator, right)
        }
        return expr
    }

    fun unary(): Expr {
        val expr = brackets()
        if (match(TokenType.BANG)) {
            return UnaryExpr(advance(), expr)
        }
        return expr
    }

    fun brackets(): Expr {
        return if (match(TokenType.LEFT_PAREN)) {
            consume(TokenType.LEFT_PAREN)
            val expr = equality()
            consume(TokenType.RIGHT_PAREN)
            expr
        } else {
            primary()
        }
    }
}

fun main(args: Array<String>) {
    val toks = Scanner().tokenize("(3 +5) ==5")
    ExprParser(toks).parse().also { Rpn().prettyPrint(it).also { println(it) } }
}