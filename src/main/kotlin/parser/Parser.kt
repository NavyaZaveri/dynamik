package parser

import expressions.*
import interpreter.TreeWalker
import interpreter.evaluateAllBy
import scanner.Tok
import scanner.TokenType
import scanner.tokenize

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
            TokenType.IDENTIFIER -> if (tokens[current + 1].type == TokenType.EQUAL) return assignStmt()
            TokenType.While -> return whileStatement()
            TokenType.FN -> return fnStmt()
            TokenType.Memo -> return fnStmt()
            TokenType.IF -> return ifStmt()
        }
        return exprStmt()
    }

    fun parseBody(): List<Stmt> {
        consume(TokenType.LEFT_BRACE)
        val body = mutableListOf<Stmt>()
        while (!match(TokenType.RIGHT_BRACE)) {
            body.add(stmt())
        }
        consume(TokenType.RIGHT_BRACE)
        return body
    }


    private fun ifStmt(): Stmt {
        consume(TokenType.IF)
        consume(TokenType.LEFT_PAREN)
        val condition = expression()
        consume(TokenType.RIGHT_PAREN)
        val body = parseBody()
        return IfStmt(condition, body)
    }

    fun whileStatement(): Stmt {
        consume(TokenType.While)
        consume(TokenType.LEFT_PAREN)
        val cond = expression()
        consume(TokenType.RIGHT_PAREN)
        val body = parseBody()

        return WhileStmt(cond, body)
    }

    fun printStmt(): Stmt {
        consume(TokenType.PRINT)
        val thingToPrint = expression()
        return PrintStmt(thingToPrint).also { consume(TokenType.SEMICOLON) }
    }

    fun assignStmt(): Stmt {
        val id = consume(TokenType.IDENTIFIER)
        consume(TokenType.EQUAL)
        val valueAssigned = expression()
        return AssignStmt(id, valueAssigned).also { consume(TokenType.SEMICOLON) }
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

    fun fnStmt(): Stmt {
        val memoized = consumeIfPresent(TokenType.Memo)
        consume(TokenType.FN)
        val name = consume(TokenType.IDENTIFIER)
        consume(TokenType.LEFT_PAREN)
        var params = mutableListOf<Tok>()
        while (!match(TokenType.RIGHT_PAREN)) {
            val param = consume(TokenType.IDENTIFIER)
            params.add(param)
            consumeIfPresent(TokenType.COMMA)
        }
        consume(TokenType.RIGHT_PAREN)
        val body = parseBody()
        return FnStmt(name, params, body, memoized)
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
            call()
        }
    }

    fun consumeIfPresent(wantedType: TokenType): Boolean {
        if (match(wantedType)) {
            consume(wantedType)
            return true
        }
        return false
    }

    fun call(): Expr {
        val args = mutableListOf<Expr>()
        if (match(TokenType.IDENTIFIER) && tokens[current + 1].type == TokenType.LEFT_PAREN) {
            val name = consume(TokenType.IDENTIFIER)
            consume(TokenType.LEFT_PAREN)
            while (!match(TokenType.RIGHT_PAREN)) {
                args.add(expression())
                consumeIfPresent(TokenType.COMMA)
            }
            consume(TokenType.RIGHT_PAREN)
            return CallExpr(name.lexeme, args = args)
        }
        return primary()
    }

}

fun List<Tok>.parseStmts(): List<Stmt> {
    return StmtParser(this).parseStmts()
}

fun List<Tok>.parse(): Expr {
    return ExprParser(this).parse()
}


fun main(args: Array<String>) {
    ("@memo fn stuff(a,b) {" +
            "print 100;} stuff(20,30); stuff(20,30); ").tokenize()
        .parseStmts()
        .evaluateAllBy(TreeWalker())
}