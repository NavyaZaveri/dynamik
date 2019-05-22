package com.github.navyazaveri.dynamik.parser

import com.github.navyazaveri.dynamik.errors.InvalidToken
import com.github.navyazaveri.dynamik.expressions.*
import com.github.navyazaveri.dynamik.interpreter.TreeWalker
import com.github.navyazaveri.dynamik.interpreter.evaluateAllBy
import com.github.navyazaveri.dynamik.scanner.Tok
import com.github.navyazaveri.dynamik.scanner.TokenType
import com.github.navyazaveri.dynamik.scanner.tokenize
import java.util.*


class StmtParser(tokens: List<Tok>) : ExprParser(tokens) {

    fun parseStmts(): List<Stmt> {
        val stmts = mutableListOf<Stmt>()
        while (!allTokensConsumed()) {
            stmts.add(stmt())
        }
        return stmts
    }


    private fun instanceStmt(): InstanceStmt {
        val name = consume(TokenType.IDENTIFIER).lexeme
        consume(TokenType.DOT)
        val behavior = stmt()
        return InstanceStmt(name, behavior)
    }

    private fun stmt(): Stmt {
        when (tokens[current].type) {
            TokenType.PRINT -> return printStmt()
            TokenType.VAR -> return varStmt()
            TokenType.VAL -> return valStmt()
            TokenType.IDENTIFIER -> {
                if (nextTokenTypeIs(TokenType.EQUAL)) return assignStmt()
                if (nextTokenTypeIs(TokenType.DOT)) {
                    // 1     2    3     1    2  3
                    //IDENT.METHOD() || IDENT.ATTRIBUTE
                    if (lookAhead(3).filter { it.type == TokenType.LEFT_PAREN }.isPresent) {
                        return exprStmt()
                    }
                    return instanceStmt()
                }
                return exprStmt()
            }
            TokenType.While -> return whileStatement()
            TokenType.FN -> return fnStmt()
            TokenType.Memo -> return fnStmt()
            TokenType.IF -> return ifStmt()
            TokenType.RETURN -> return returnStmt()
            TokenType.FOR -> return forStmt()
            TokenType.Par -> return parStmt()
            TokenType.Wait -> return waitStmt()
            TokenType.GLOBAL -> return globalStmt()
            TokenType.ASSERT -> return assertStmt()
            TokenType.SLASH -> {
                if (nextTokenTypeIs(TokenType.STAR)) return multiLineComment()
                if (nextTokenTypeIs(TokenType.SLASH)) return singleLineComment()
                throw InvalidToken("${tokens[current].lexeme} cannot be placed after ${previous().lexeme}")
            }
            TokenType.PAR_WITH_LOCK -> return parLockStmt()
            TokenType.CLASS -> return classStmt()
            TokenType.THIS -> return thisStmt()
            else -> return exprStmt()
        }
    }

    fun thisStmt(): Stmt {
        consume(TokenType.THIS)
        consume(TokenType.DOT)
        val behavior = stmt()
        return ThisStmt(behavior)
    }

    private fun classStmt(): Stmt {
        consume(TokenType.CLASS)
        val tokenName = consume(TokenType.IDENTIFIER)
        val hasFields = consumeIfPresent(TokenType.LEFT_PAREN)
        var fields = mutableListOf<String>()
        if (hasFields) {
            fields = consumeTokens().map { it.lexeme }.toMutableList()
            consume(TokenType.RIGHT_PAREN)
        }

        consume(TokenType.LEFT_BRACE)
        val methods = mutableListOf<FnStmt>()
        while (!match(TokenType.RIGHT_BRACE)) {
            methods.add(fnStmt())
        }
        consume(TokenType.RIGHT_BRACE)

        return ClassStmt(tokenName.lexeme, methods, fields)
    }

    private fun parLockStmt(): Stmt {
        consume(TokenType.PAR_WITH_LOCK)
        val callExpr = call()
        when (callExpr) {
            is CallExpr -> return ParStmt(callExpr, lock = true).also { consume(TokenType.SEMICOLON) }
            else -> throw RuntimeException("par needs to be followed by a function invocation.")
        }
    }

    private fun multiLineComment(): Stmt {
        consume(TokenType.SLASH)
        consume(TokenType.STAR)

        // keep consuming tokens until we find the ending slash
        while (!consumeIfPresent(TokenType.SLASH)) {
            advance()
        }
        return SkipStmt()
    }

    /**
     * Throws all tokens present in the given line
     */
    private fun singleLineComment(): SkipStmt {
        val currentLine = tokens[current].line
        consume(TokenType.SLASH)
        consume(TokenType.SLASH)
        while (!allTokensConsumed() && tokens[current].line == currentLine) {
            advance()
        }
        return SkipStmt()
    }


    /**
     * Captures as assert statement, for eg: ```assert(x==1)```;
     */
    private fun assertStmt(): AssertStmt {
        consume(TokenType.ASSERT)
        consume(TokenType.LEFT_PAREN)
        val expr = expression()
        consume(TokenType.RIGHT_PAREN)
        return AssertStmt(expr).also { consume(TokenType.SEMICOLON) }
    }

    private fun globalStmt(): Stmt {
        consume(TokenType.GLOBAL)
        consume(TokenType.VAL)
        val name = consume(TokenType.IDENTIFIER)
        consume(TokenType.EQUAL)
        val valueAssigned = expression()
        return GlobalStmt(name, valueAssigned).also { consume(TokenType.SEMICOLON) }
    }

    private fun waitStmt(): WaitStmt {
        consume(TokenType.Wait)
        return WaitStmt().also { consume(TokenType.SEMICOLON) }
    }

    private fun parStmt(): Stmt {
        consume(TokenType.Par)
        val callExpr = call()
        when (callExpr) {
            is CallExpr -> return ParStmt(callExpr).also { consume(TokenType.SEMICOLON) }
            else -> throw RuntimeException("par needs to be followed by a function invocation.")
        }
    }

    private fun returnStmt(): Stmt {
        consume(TokenType.RETURN)
        val emptyReturn = consumeIfPresent(TokenType.SEMICOLON)
        var stmt: Stmt? = null
        if (!emptyReturn) {
            stmt = stmt()
        }
        return ReturnStmt(stmt!!)
    }

    private fun forStmt(): ForStmt {
        consume(TokenType.FOR)
        consume(TokenType.LEFT_PAREN)
        val init = stmt()
        val cond = expression()
        consume(TokenType.SEMICOLON)
        val intermediate = assignStmt(false)
        consume(TokenType.RIGHT_PAREN)
        val body = parseBody() + listOf(intermediate)
        return ForStmt(init, cond, body)
    }

    private fun parseBody(): List<Stmt> {
        consume(TokenType.LEFT_BRACE)
        val body = mutableListOf<Stmt>()
        while (!match(TokenType.RIGHT_BRACE)) {
            body.add(stmt())
        }
        consume(TokenType.RIGHT_BRACE)
        return body
    }

    private fun ifStmt(): IfStmt {
        consume(TokenType.IF)
        consume(TokenType.LEFT_PAREN)
        val condition = expression()
        consume(TokenType.RIGHT_PAREN)
        val body = parseBody()
        if (consumeIfPresent(TokenType.ELSE)) {
            val elseBody = parseBody()
            return IfStmt(condition, body, elseBody)
        }

        return IfStmt(condition, body)
    }

    private fun whileStatement(): WhileStmt {
        consume(TokenType.While)
        consume(TokenType.LEFT_PAREN)
        val cond = expression()
        consume(TokenType.RIGHT_PAREN)
        val body = parseBody()

        return WhileStmt(cond, body)
    }

    private fun printStmt(): PrintStmt {
        consume(TokenType.PRINT)
        val thingToPrint = stmt()
        return PrintStmt(thingToPrint)
            .also { consumeIfPresent(TokenType.SEMICOLON) }
    }

    private fun assignStmt(consumeSemicolon: Boolean = true): AssignStmt {

        val id = consume(TokenType.IDENTIFIER)
        consume(TokenType.EQUAL)
        val valueAssigned = expression()
        return AssignStmt(id, valueAssigned)
            .also { if (consumeSemicolon) consume(TokenType.SEMICOLON) }
    }

    private fun exprStmt(): Stmt {
        return ExprStmt(expression()
            .also { consumeIfPresent(TokenType.SEMICOLON) })
    }

    private fun varStmt(): Stmt {
        consume(TokenType.VAR)
        val name = consume(TokenType.IDENTIFIER)
        consume(TokenType.EQUAL)

        val valueAssigned = expression()
        return VarStmt(name, valueAssigned)
            .also { consume(TokenType.SEMICOLON) }
    }

    /**
     * Capture a function declaration, its parameters, and its body
     */
    private fun fnStmt(): FnStmt {
        val memoized = consumeIfPresent(TokenType.Memo)
        consume(TokenType.FN)
        val name = consume(TokenType.IDENTIFIER)
        consume(TokenType.LEFT_PAREN)
        val params = mutableListOf<Tok>()
        var argLeftToConsume = true
        while (!match(TokenType.RIGHT_PAREN) && argLeftToConsume) {
            val param = consume(TokenType.IDENTIFIER)
            params.add(param)
            argLeftToConsume = consumeIfPresent(TokenType.COMMA)
        }
        consume(TokenType.RIGHT_PAREN)
        val body = parseBody()
        return FnStmt(name, params, body, memoized)
    }

    /**
     * Captures a non-reassignable declaration
     */
    private fun valStmt(): ValStmt {
        consume(TokenType.VAL)
        val name = consume(TokenType.IDENTIFIER)
        consume(TokenType.EQUAL)
        val valueAssigned = expression()
        return ValStmt(name, valueAssigned)
            .also { consume(TokenType.SEMICOLON) }
    }
}

open class ExprParser(val tokens: List<Tok>) {
    var current = 0

    fun concat(): Expr {
        if (tokens[current].type == TokenType.IDENTIFIER && nextTokenTypeIs(TokenType.PLUS_PLUS)) {
            val exprContainers = mutableListOf<Expr>()
            val c = call()
            exprContainers.add(c)
            while (!match(TokenType.SEMICOLON)) {
                consume(TokenType.PLUS_PLUS)
                val c = call()
                exprContainers.add(c)
            }
            return ConcatExpr(exprContainers)
        }

        return addition()
    }

    fun parse(): Expr {
        return assignExpr()
    }

    fun assignAble(index: Int): Boolean {
        return !allTokensConsumed() && (tokens[index].type == TokenType.IDENTIFIER)
                || (tokens[index].type == TokenType.DOT && assignAble(index + 1))
    }

    fun consumeAssignable() {
        if (tokens[current].type == TokenType.IDENTIFIER) {

        }
    }

    fun assignExpr(): Expr {
        if (tokens[current].type == TokenType.IDENTIFIER && nextTokenTypeIs(TokenType.EQUAL)) {
            val ident = consume(TokenType.IDENTIFIER)
            consume(TokenType.EQUAL)
            val rhs = expression()
            return AssignExpr(ident, rhs)
        }
        return expression()
    }

    fun expression(): Expr {
        return booleanOp()
    }

    fun booleanOp(): Expr {
        var left = equality()
        while (match(TokenType.AND_AND)) {
            val op = consume(TokenType.AND_AND)
            val right = equality()
            left = BinaryExpr(left, op, right)
        }
        return left
    }

    private fun equality(): Expr {
        var expr = comparison()
        while (match(TokenType.EQUAL_EQUAL, TokenType.BANG_EQUAL)) {
            val operand = advance()
            val right = comparison()
            expr = BinaryExpr(expr, operand, right)
        }
        return expr
    }

    private fun comparison(): Expr {
        var expr = concat()
        while (match(TokenType.LESS_EQUAL, TokenType.GREATER_EQUAL, TokenType.LESS, TokenType.GREATER)) {
            val operand = advance()
            val right = comparison()
            expr = BinaryExpr(expr, operand, right)
        }
        return expr
    }

    fun previous() = tokens[current - 1]

    /**
     * [primary] matches against a single token, then advances the current token pointer .
     */
    private fun primary(): Expr {
        advance()
        return when (previous().type) {
            TokenType.NUMBER -> LiteralExpr(token = previous())
            TokenType.STRING -> LiteralExpr(token = previous())
            TokenType.IDENTIFIER -> VariableExpr(token = previous())
            TokenType.TRUE -> LiteralExpr(token = previous())
            TokenType.False -> LiteralExpr(token = previous())
            TokenType.THIS -> {
                consume(TokenType.DOT)
                val expr = expression()
                return ThisExpr(expr)
            }
            else -> throw InvalidToken("could not recognize ${previous()} at line ${tokens[current - 2].line}")
        }
    }

    fun advance(): Tok = tokens[current].also { current += 1 }

    /**
     * @returns a [Token] and advances the current token pointer
     */
    fun consume(tokType: TokenType): Tok {
        if (allTokensConsumed()) {
            throw RuntimeException("expecting  $tokType after ${previous().lexeme}")
        }
        if (tokens[current].type != tokType) {
            throw RuntimeException("expecting $tokType, found ${tokens[current].type} instead at line ${tokens[current].line}")
        }
        return tokens[current].also { current += 1 }
    }

    fun allTokensConsumed(): Boolean = current >= tokens.size

    /**
     * Checks if the current token is any of the given [tokenTypes]
     */
    fun match(vararg tokenTypes: TokenType): Boolean =
        tokenTypes.any { !allTokensConsumed() && it == tokens[current].type }

    /**
     * Captures addition and subtraction
     * Add ->  Mul (('+'| '-')  Mul)*
     */
    private fun addition(): Expr {
        var expr = multiplication()
        while (match(TokenType.PLUS, TokenType.MINUS)) {
            val operator = advance()
            val right = multiplication()
            expr = BinaryExpr(expr, operator, right)
        }
        return expr
    }

    /*
     ** Captures multiplication and division.
     * Mul -> Mul ('*' Unary) *
    */
    private fun multiplication(): Expr {
        var expr = unary()
        while (match(TokenType.STAR, TokenType.SLASH)) {
            val operator = advance()
            val right = unary()
            expr = BinaryExpr(expr, operator, right)
        }
        return expr
    }

    private fun unary(): Expr {
        if (match(TokenType.BANG, TokenType.MINUS)) {
            val minusOrBang = advance()
            val expr = brackets()
            return UnaryExpr(minusOrBang, expr)
        }
        return brackets()
    }

    private fun brackets(): Expr {
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

    fun consumeTokens(): List<Tok> {
        val args = mutableListOf<Tok>()
        while (!match(TokenType.RIGHT_PAREN)) {
            args.add(tokens[current])
            val comma = consumeIfPresent(TokenType.COMMA)
            if (!comma) {
                current += 1
            }
        }
        return args.filter { it.type != TokenType.COMMA }
    }

    private fun consumeArgs(): List<Expr> {
        val args = mutableListOf<Expr>()
        while (!match(TokenType.RIGHT_PAREN)) {
            args.add(expression())
            consumeIfPresent(TokenType.COMMA)
        }
        return args
    }

    fun lookAhead(inc: Int = 1): Optional<Tok> {
        if (current + inc < tokens.size) {
            return Optional.of(tokens[current + inc])
        }
        return Optional.empty()
    }

    fun call(): Expr {
        if (match(TokenType.IDENTIFIER) && lookAhead().filter { it.type == TokenType.LEFT_PAREN }.isPresent) {
            val name = consume(TokenType.IDENTIFIER)
            consume(TokenType.LEFT_PAREN)
            val args = consumeArgs()
            consume(TokenType.RIGHT_PAREN)
            return CallExpr(name.lexeme, args = args)
        }
        return instance()
    }

    fun nextTokenTypeIs(t: TokenType): Boolean {
        return lookAhead().filter { it.type == t }.isPresent
    }


    // . -> call | instance | primary
    private fun instance(): Expr {
        if (!allTokensConsumed() && match(TokenType.IDENTIFIER) && nextTokenTypeIs(TokenType.DOT)) {
            val className = consume(TokenType.IDENTIFIER).lexeme
            consume(TokenType.DOT)
            try {
                val expr = call()
                return InstanceExpr(className, expr)
            } catch (e: Exception) {
                try {
                    val expr = expression()
                    return InstanceExpr(className, expr)
                } catch (e: Exception) {
                    val expr = expression()
                    return InstanceExpr(className, expr)
                }
            }
        }
        return primary()
    }
}

fun List<Tok>.parseStmts(): List<Stmt> {
    return StmtParser(this).parseStmts()
}

fun List<Tok>.parseExpr(): Expr {
    return ExprParser(this).parse()
}

fun main(args: Array<String>) {


    ("class Math(x) { fn hello() { print  1;} fn bar() { this.hello(); this.x = 100;} }" +
            "val m = Math(20);"
            + "m.bar();" +
            "print m.x;"
            ).tokenize()
        .parseStmts()
        .evaluateAllBy(TreeWalker())
}
