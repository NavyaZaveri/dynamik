import errors.VariableNotInScope
import interpreter.Repl
import interpreter.TreeWalker
import org.junit.Test
import parser.parseExpr
import parser.parseStmts
import scanner.tokenize

class InterpreterTest {
    val repl = Repl()

    @Test
    fun testArithmetic() {
        val actual = "3+(5+6)*6".tokenize().parseExpr().evaluateBy(TreeWalker())
        val expected = 69.0
        assert(actual == expected) { "actual = $actual, expected=$expected" }
    }


    @Test
    fun testArithmeticWithTrickyBrackets() {
        val actual = "5*(6+(3*1))".tokenize().parseExpr().evaluateBy(TreeWalker())
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
    fun testReturn() {
        val stmts = "fn foo() { return 20;} var d = foo(); d;".tokenize().parseStmts()
        val repl = Repl()
        val actual = repl.eval(stmts)
        val expected = 20.0
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
        val stmts = ("@memo fn fib(n) {" +
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
    fun testVariableReassignment() {
        val stmts = "var x = 3; x= 4; x;".tokenize().parseStmts()
        val actual = repl.eval(stmts)
        val expected = 4.0
        assert(actual == expected) { "actual = $actual, expected = $expected" }
    }

    @Test
    fun testSameVarNameInDifferentScopes() {
        val stmts = ("fn hello() {" +
                "val x = 3;}" +
                "val x = 100;" +
                "hello();" +
                "x;").tokenize().parseStmts()
        val actual = repl.eval(stmts)
        val expected = 100.0
        assert(actual == expected) { "actual = $actual, expected = $expected" }
    }

    @Test
    fun testSideEffectInWhileLoop() {
        val stmts = "var x =3; var y =1; while (x>0) { x =  x-1; y = y+1;} y;".tokenize().parseStmts()
        val actual = repl.eval(stmts)
        val expected = 4.0
        assert(actual == expected) { "actual = $actual, expected = $expected" }
    }

    @Test
    fun testForLoopEquivalenceToWhileLoop() {
        val stmts =
            "var x =3; while (x>2) { x = x -1;} var y = 3; var i = 0;  for (i=0;i<1;i = i+1){ y = y-1; } x==y;"
                .tokenize().parseStmts()
        val actual = repl.eval(stmts)
        val expected = true
        assert(actual == expected) { "actual = $actual, expected = $expected" }

    }

    @Test
    fun testEnvironmentLeak() {
        val stmts = "fn foo() { val x = 2;} x;".tokenize().parseStmts()
        var exceptionCaught = false
        try {
            repl.eval(stmts)
        } catch (v: VariableNotInScope) {
            exceptionCaught = true
        }

        assert(exceptionCaught == true, { "environment leak not caught!" });

    }
}
