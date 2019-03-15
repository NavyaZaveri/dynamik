import interpreter.TreeWalker
import org.junit.Test
import parser.parse
import parser.parseStmts
import scanner.tokenize

class InterpreterTest {

    @Test
    fun testArithmetic() {
        val actual = "3+(5+6)*6".tokenize().parse().evaluateBy(TreeWalker())
        val expected = 69.0
        assert(actual == expected) { "actual = $actual, expected=$expected" }
    }

    @Test
    fun testArithmeticWithTrickyBrackets() {
        val actual = "5*(6+(3*1))".tokenize().parse().evaluateBy(TreeWalker())
        val expected = 45.0
        assert(actual == expected) { "actual = $actual, expected = $expected" }
    }

    @Test
    fun testArithmeticWithVariables() {
        val stmts = "var x = 3; var y= 5; (x*(y+1));".tokenize().parseStmts()
        val interpreter = TreeWalker()
        interpreter.evaluate(stmts[0])
        interpreter.evaluate(stmts[1])
        val actual = interpreter.evaluate(stmts[2])
        val expected = 18.0
        assert(actual == expected) { "actual = $actual, expected = $expected" }
    }
}