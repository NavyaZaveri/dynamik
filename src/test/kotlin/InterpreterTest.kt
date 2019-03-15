import interpreter.TreeWalker
import org.junit.Test
import parser.parse
import scanner.tokenize

class InterpreterTest {

    @Test
    fun testArithmetic() {
        val actual = "3+(5+6)*6".tokenize().parse().evaluateBy(TreeWalker())
        val expected = 69.0
        assert(actual == expected) { "actual = $actual, expected=$expected" }
    }
}