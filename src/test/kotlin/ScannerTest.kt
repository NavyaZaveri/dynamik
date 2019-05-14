import org.junit.Assert.assertTrue
import org.junit.Test
import com.github.navyazaveri.dynamik.scanner.Scanner
import com.github.navyazaveri.dynamik.scanner.TokenType
import com.github.navyazaveri.dynamik.scanner.tokenize

class ScannerTest {
    private val s = Scanner()

    @Test
    fun testTruthTokenIdentification() {
        val code = "val x = true;"
        val tokens = s.tokenize(code)
        assertTrue(tokens[tokens.size - 2].type == TokenType.TRUE)
    }

    @Test
    fun testEqualsPrecedence() {
        val codeWithDoubleEquals = "x == 3"
        val tokens = s.tokenize(codeWithDoubleEquals)
        assertTrue(tokens[tokens.size - 2].type == TokenType.EQUAL_EQUAL)
    }

    @Test
    fun testStringExtraction() {
        val code = "print \"hello world\""
        val tokens = s.tokenize(code)
        val expected = "hello world"
        val actual = tokens[tokens.size - 1].literal
        assertTrue("actual = $actual, expected = $expected", actual == expected)
    }

    @Test
    fun testBoundaryMatching() {
        val toks = "val thing = value".tokenize();
        assertTrue(toks[toks.size - 1].type == TokenType.IDENTIFIER)
    }
}