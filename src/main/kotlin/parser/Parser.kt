package parser

import expressions.*
import interpreter.Rpn
import scanner.Scanner
import scanner.Tok
import scanner.TokenType
import scanner.tokenize
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

class StmtParser(tokens: List<Tok>) : ExprParser(tokens) {

    fun parseStmts(): List<Stmt> {
        val stmt = mutableListOf<Stmt>()
        while (!allTokensConsumed()) {
            stmt.add(stmt())
        }
        return stmt
    }

    fun stmt(): Stmt {
        when (tokens[current].type) {
            TokenType.PRINT -> return printStmt()
            TokenType.VAR -> return varStmt()
            TokenType.VAL -> return valStmt()
            TokenType.IDENTIFIER -> return assignStmt()
            TokenType.While -> return whileStatement()
        }
        return exprStmt()
    }

    fun whileStatement(): Stmt {
        consume(TokenType.While)
        consume(TokenType.LEFT_PAREN)
        val cond = expression()
        consume(TokenType.RIGHT_PAREN)
        consume(TokenType.LEFT_BRACE)
        val stmts = mutableListOf<Stmt>()
        while (!match(TokenType.RIGHT_BRACE)) {
            stmts.add(stmt())
        }
        consume(TokenType.RIGHT_BRACE)

        return WhileStmt(cond, stmts)
    }

    fun printStmt(): Stmt {
        consume(TokenType.PRINT)
        val thingToPrint = expression()
        return PrintStmt(thingToPrint).also { consume(TokenType.SEMICOLON) }
    }

    fun assignStmt(): Stmt {
        val id = consume(TokenType.IDENTIFIER)
        consume(TokenType.EQUAL)
        val valueAssined = expression()
        return AssignStmt(id, valueAssined).also { consume(TokenType.SEMICOLON) }
    }

    fun exprStmt(): Stmt {
        return ExprStmt(expression()).also { consume(TokenType.SEMICOLON) }
    }

    fun varStmt(): Stmt {
        consume(TokenType.VAR)
        val name = consume(TokenType.IDENTIFIER)
        consume(TokenType.EQUAL)

        val valueAssigned = expression()
        return VarStmt(name, valueAssigned).also { consume(TokenType.SEMICOLON) }
    }

    fun valStmt(): Stmt {
        consume(TokenType.VAL)
        val name = consume(TokenType.IDENTIFIER)
        consume(TokenType.EQUAL)
        val valueAssigned = expression()
        return ValStmt(name, valueAssigned).also { consume(TokenType.SEMICOLON) }

    }
}

open class ExprParser(val tokens: List<Tok>) {
    var current = 0

    fun parse(): Expr {
        return expression()
    }

    fun expression(): Expr {
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
            TokenType.IDENTIFIER -> VariableExpr(token = tokens[current])
            TokenType.TRUE -> LiteralExpr(token = tokens[current])
            TokenType.False -> LiteralExpr(token = tokens[current])
            else -> throw RuntimeException("could not recognize ${tokens[current]}")
        }.also { advance() }
    }

    private fun advance(): Tok = tokens[current].also { current += 1 }

    fun consume(tokType: TokenType): Tok {
        if (allTokensConsumed()) {
            throw RuntimeException("expecting  $tokType after ${tokens[current - 1].lexeme}")
        }
        if (tokens[current].type != tokType) {
            throw RuntimeException("expecting $tokType, found ${tokens[current].type} instead")
        }
        return tokens[current].also { current += 1 }
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
            val expr = expression()
            consume(TokenType.RIGHT_PAREN)
            expr
        } else {
            primary()
        }
    }
}

fun List<Tok>.parseStmts(): List<Stmt> {
    return StmtParser(this).parseStmts()
}

fun List<Tok>.parse(): Expr {
    return ExprParser(this).parse()
}

fun main(args: Array<String>) {
    val toks = Scanner().tokenize("(3 +(5)) ==8")
    ExprParser(toks).parse().also { Rpn().prettyPrint(it).also { println(it) } }

    val ts = Scanner().tokenize("val x = 3+10; print \"hello\"; ")
    val stm = StmtParser(ts).parseStmts()
    stm.forEach { it.evaluateBy(Rpn()).also { println(it) } }
    "while (true) { var x = 1; var y =3;}".tokenize().parseStmts().forEach { it.evaluateBy(Rpn()).also { println(it) } }
}