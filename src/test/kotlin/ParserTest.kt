import interpreter.Rpn
import org.junit.Assert.assertTrue
import org.junit.Test
import parser.Expr
import parser.ExprParser
import scanner.Scanner
import scanner.Tok

class ParserTest {
    fun parse(toks: List<Tok>): Expr {
        return ExprParser(toks).parse()
    }


    @Test
    fun testBracketPrecedence() {
        val srcCode = "(3+5)*4"
        val toks = Scanner().tokenize(srcCode)
        val expr = parse(toks)
        val expected = "3.0 5.0 + 4.0 *"
        val actual = Rpn().prettyPrint(expr)
        assertTrue("expected $expected, actual = $actual", expected == actual)
    }
}