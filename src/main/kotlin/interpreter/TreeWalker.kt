package interpreter

import parser.*
import scanner.Scanner
import scanner.TokenType
import java.lang.RuntimeException

class TreeWalker : Visitor<Any> {
    fun evalauate(expr: Expr): Any {
        return expr.accept(this)
    }


    override fun visitBinaryExpression(expr: BinaryExpr): Any {
        val left = evalauate(expr.left)
        val right = evalauate(expr.right)
        when (expr.operand.type) {
            TokenType.PLUS -> return left as Double + right as Double
            TokenType.MINUS -> return left as Double - right as Double
            TokenType.STAR -> return left as Double * right as Double
        }
        throw RuntimeException()
    }

    override fun visitUnaryExpression(expr: UnaryExpr): Any {
        val l = evalauate(expr.left)
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
    val toks = Scanner().tokenize("(3+5)*-73 ")
    val ast = ExprParser(toks).parse()
    TreeWalker().evalauate(ast).also { println(it) }
}