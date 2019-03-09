import org.junit.Before
import org.junit.Test
import scanner.Scanner
import scanner.TokenType


class ScannerTest {
    val s = Scanner()


    @Test
    fun testTruthyTokenIdenitifcation() {
        val code = "val x = true;"
        val tokens = s.tokenize(code)
        assert(tokens[tokens.size - 2].type == TokenType.TRUE)
    }
}