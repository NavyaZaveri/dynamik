package interpreter

import expressions.BinaryExpr
import expressions.Expr
import expressions.LiteralExpr
import expressions.UnaryExpr
import parser.*
import scanner.Scanner
import scanner.TokenType
import java.lang.RuntimeException


class TreeWalker : ExpressionVisitor<Any> {
    fun evaluate(expr: Expr): Any {
        return expr.accept(this)
    }

    private fun isBoolean(vararg things: Any) = things.all { it is Boolean }


    override
    fun visitBinaryExpression(expr: BinaryExpr): Any {
        val left = evaluate(expr.left)
        val right = evaluate(expr.right)
        when (expr.operand.type) {
            TokenType.PLUS -> return left as Double + right as Double
            TokenType.MINUS -> return left as Double - right as Double
            TokenType.STAR -> return left as Double * right as Double
            TokenType.EQUAL_EQUAL -> return left == right
            TokenType.AND -> if (isBoolean(
                    left,
                    right
                )
            ) return left as Boolean && right as Boolean else throw RuntimeException()

        }
        throw RuntimeException()
    }

    override fun visitUnaryExpression(expr: UnaryExpr): Any {
        val l = evaluate(expr.left)
        when (expr.token.type) {
            TokenType.MINUS -> return -(l as Double)
        }
        TODO()
    }

    override fun visitLiteralExpression(expr: LiteralExpr): Any {
        return expr.token.literal
    }
}

fun main(args: Array<String>) {
    val toks = Scanner().tokenize("9+10==19")
    val ast = ExprParser(toks).parse()
    TreeWalker().evaluate(ast).also { println(it) }
}