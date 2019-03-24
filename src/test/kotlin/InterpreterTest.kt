import interpreter.Repl
import interpreter.TreeWalker
import interpreter.evaluateAllBy
import org.junit.Test
import parser.parse
import parser.parseStmts
import scanner.tokenize

class InterpreterTest {
    val repl = Repl()

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

    @Test
    fun testRecursiveFib() {
        val stmts = (" fn fib(n) {" +
                "if (n<2) { return 1;}" +
                " return  fib(n-1) + fib(n-2);" +
                "}" +
                "val d = fib(3);" +
                "d;").tokenize()
            .parseStmts()
        val actual = repl.eval(stmts)
        val expected = 3.0
        assert(actual == expected) { "actual = $actual, expected = $expected" }
    }


    @Test
    fun testRecursiveFibWithMemo() {
        val stmts = (" f@memo n fib(n) {" +
                "if (n<2) { return 1;}" +
                " return  fib(n-1) + fib(n-2);" +
                "}" +
                "val d = fib(3);" +
                "d;").tokenize()
            .parseStmts()
        val actual = repl.eval(stmts)
        val expected = 3.0
        assert(actual == expected) { "actual = $actual, expected = $expected" }
    }
}