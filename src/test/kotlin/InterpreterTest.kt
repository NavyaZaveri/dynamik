import interpreter.TreeWalker
import interpreter.evaluate
import jdk.internal.org.objectweb.asm.tree.analysis.Interpreter
import org.junit.Test
import parser.ExprParser
import parser.parseStmts
import scanner.Scanner
import scanner.tokenize

class InterpreterTest {

    @Test
    fun testArithmetic() {
        val toks = Scanner().tokenize("3+(5+6)*6")
        val expr = ExprParser(toks).parse()
        val expected = 69
        val actual = expr.evaluateBy(TreeWalker())
        assert(actual == expected, { "actual = $actual, expected=$expected" })
    }
}