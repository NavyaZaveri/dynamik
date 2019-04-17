import com.github.NavyaZaveri.dynamik.interpreter.Rpn
import org.junit.Assert.assertTrue
import org.junit.Test
import com.github.NavyaZaveri.dynamik.expressions.Expr
import parser.ExprParser
import com.github.NavyaZaveri.dynamik.scanner.Scanner
import com.github.NavyaZaveri.dynamik.scanner.Tok

class ParserTest {
    fun parse(toks: List<Tok>): Expr {
        return ExprParser(toks).parse()
    }

    @Test
    fun testMultiplicationPrecedence() {
        val srcCode = "3*4+5"
        val toks = Scanner().tokenize(srcCode)
        val expr = parse(toks)
        val expected = "3.0 4.0 * 5.0 +"
        val actual = Rpn().prettyPrint(expr)
        assertTrue("expected $expected, actual = $actual", expected == actual)
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
