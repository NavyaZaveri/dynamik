import com.github.navyazaveri.dynamik.errors.AssertionErr
import com.github.navyazaveri.dynamik.errors.VariableNotInScope
import com.github.navyazaveri.dynamik.interpreter.Repl
import com.github.navyazaveri.dynamik.interpreter.TreeWalker
import com.github.navyazaveri.dynamik.interpreter.evaluateAllBy
import org.junit.Test
import com.github.navyazaveri.dynamik.parser.parseExpr
import com.github.navyazaveri.dynamik.parser.parseStmts
import com.github.navyazaveri.dynamik.scanner.tokenize
import org.junit.After
import org.junit.Before

class InterpreterTest {
    lateinit var repl: Repl

    @Before
    fun setUp() {
        repl = Repl()
    }

    @After
    fun tearDown() {
        repl.clear()
    }

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
        val actual = repl.eval(stmts)
        val expected = 20.0
        assert(actual == expected) { "actual = $actual, expected = $expected" }
    }

    @Test
    fun testRecursiveFib() {
        val stmts = (" fn fib(n) {" +
                "if (n<2) { return 1;}" +
                " return  fib(n-1) + fib(n-2);" +
                "} " +
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
                "if (n==0) {return 0;}" +
                "if (n==1) { return 1;}" +
                " return  fib(n-1) + fib(n-2);" +
                "} " +
                "val d = fib(7);" +
                "d;").tokenize()
            .parseStmts()

        val actual = repl.eval(stmts)
        val expected = 13.0
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
        val repl = Repl()
        val actual = repl.eval(stmts)
        val expected = true
        assert(actual == expected) { "actual = $actual, expected = $expected" }
    }

    @Test
    fun testEnvironmentLeak() {
        val stmts = "fn foo() { val x = 2;} x;".tokenize().parseStmts()
        var leak = false
        try {
            repl.eval(stmts)
        } catch (v: VariableNotInScope) {
            leak = true
        }

        assert(leak) { "environment leak not caught!" }
    }

    @Test
    fun testGlobalVariableExistence() {
        val stmts = ("fn foo() { return x; } @global val x= 3; foo(); val c = foo(); c;")
            .tokenize().parseStmts()
        val expected = 3.0
        val repl = Repl()
        val actual = repl.eval(stmts)
        assert(actual == expected) { "actual = $actual, expected = $expected" }
    }

    @Test
    fun testAssertions() {
        var errorThrown = false
        try {
            "val x =3; val y = 4; assert (x == y);".tokenize().parseStmts().evaluateAllBy(TreeWalker())
        } catch (err: AssertionErr) {
            errorThrown = true
        }
        assert(errorThrown) { "assertion error not caught!" }
    }

    @Test
    fun testMultiLineComments() {
        var errorThrown = false
        try {
            "/* val x = 2; / print x;".tokenize().parseStmts().evaluateAllBy(TreeWalker())
        } catch (err: VariableNotInScope) {
            errorThrown = true
        }
        assert(errorThrown) { "comment parsing error!" }
    }

    @Test
    fun testIfStatements() {
        val stmts =
            "fn add(x, y) { return x +y;}" +
                    "val x = 3; " +
                    "var flag = 0; val res = add(x,4); " +
                    "if (res==7) {flag = 1;} flag;"
        val actual = repl.eval(stmts)
        val expected = 1.0
        assert(actual == expected) { "actual = $actual, expected = $expected" }
    }

    @Test
    fun testBasicClass() {
        val stmts = ("class Calculator {" +
                "fn add(x, y) { return x+y;}" +
                "fn mul(x,y) { return x* y; }" +
                "}" +
                "val calc = Calculator();" +
                "val res = calc.mul(10,20);" + "" +
                "res;").tokenize().parseStmts()
        val actual = repl.eval(stmts)
        val expected = 200.0
        assert(actual == expected) { "actual = $actual, expected = $expected" }
    }

    @Test
    fun testNestedFunction() {
        val stmts = ("fn add_and_inc(x,y) { fn inc(x) {" + "return x+1; }" +
                " val p = x+y;" +
                " return inc(p);" +
                "}" +
                "val res = add_and_inc(3,4);" +
                "res;" +
                "").tokenize().parseStmts()
        val actual = repl.eval(stmts)
        val expected = 8.0
        assert(actual == expected) { "actual = $actual, expected = $expected" }

    }

    @Test
    fun testClassFieldMutation() {
        val stmts = ("class Thing(value) {}" +
                "val foo = Thing(20);" +
                "foo.value = 111;" +
                "foo.value;").tokenize().parseStmts()
        val actual = repl.eval(stmts)
        val expected = 111.0
        assert(actual == expected) { "actual = $actual, expected = $expected" }
    }

}

