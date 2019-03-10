import interpreter.Rpn
import org.junit.Assert.assertTrue
import org.junit.Test
import parser.BinaryExpr
import parser.LiteralExpr
import scanner.Tok
import scanner.TokenType
import kotlin.math.exp

class PrettyPrinterTest {
    val rpn = Rpn()
    @Test
    fun testValidInterpretationForLiterals() {
        val expr = LiteralExpr.create { token = Tok(TokenType.NUMBER, "3", 3.0) }
        val expected = "3.0"
        val actual = rpn.prettyPrint(expr)
        assertTrue("expected = $expected, actual = $actual", expected == actual)
    }

    @Test
    fun testValidInterpretationForBinaryExprs() {
        val expr = BinaryExpr.create {
            left = LiteralExpr.create { token = Tok(TokenType.NUMBER, "5", 5.0) }
            operand = Tok(TokenType.PLUS, "+", "+")
            right = LiteralExpr.create { token = Tok(TokenType.NUMBER, "6", 6.0) }
        }
        val expected = "(+ 5.0 6.0)"
        val actual = rpn.prettyPrint(expr)
        assertTrue("expected = $expected, actual = $actual", expected == actual)
    }
}