import org.junit.Assert.assertTrue
import org.junit.Test
import scanner.Scanner
import scanner.TokenType


class ScannerTest {
    private val s = Scanner()


    @Test
    fun testTruthyTokenIdentification() {
        val code = "val x = true;"
        val tokens = s.tokenize(code)
        assertTrue(tokens[tokens.size - 2].type == TokenType.TRUE)
    }

    @Test
    fun testEqualsPrecedence() {
        val codeWithDoubleEquals = "x == 3"
        var tokens = s.tokenize(codeWithDoubleEquals)
        assertTrue(tokens[tokens.size - 2].type == TokenType.EQUAL_EQUAL)
        val codeWithsingleEquals = "x =3 "
        tokens = s.tokenize(codeWithsingleEquals)
        assertTrue(tokens[tokens.size - 2].type == TokenType.EQUAL)
    }

}