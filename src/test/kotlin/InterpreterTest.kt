import com.github.navyazaveri.dynamik.errors.AssertionErr
import com.github.navyazaveri.dynamik.errors.VariableNotInScope
import com.github.navyazaveri.dynamik.interpreter.Repl
import com.github.navyazaveri.dynamik.interpreter.TreeWalker
import com.github.navyazaveri.dynamik.interpreter.evaluateAllBy
import com.github.navyazaveri.dynamik.parser.parseExpr
import com.github.navyazaveri.dynamik.parser.parseStmts
import com.github.navyazaveri.dynamik.scanner.tokenize
import org.junit.After
import org.junit.Before
import org.junit.Test

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
    fun testClassFieldMutationFromOuterScope() {
        val stmts = ("class Thing(value) {}" +
                "val foo = Thing(20);" +
                "foo.value = 111;" +
                "foo.value;").tokenize().parseStmts()
        val actual = repl.eval(stmts)
        val expected = 111.0
        assert(actual == expected) { "actual = $actual, expected = $expected" }
    }

    @Test
    fun testFieldMutationWithinClassMethod() {
        val stmts = ("class Foo(bar) {" +
                "fn mut_bar() { bar = 101;}" +
                "}" +
                "val f = Foo(20);" +
                "f.mut_bar();" +
                "f.bar;").tokenize().parseStmts()
        val actual = repl.eval(stmts)
        val expected = 101.0
        assert(actual == expected) { "actual = $actual, expected = $expected" }

    }

    @Test
    fun testChainedMethodCalls() {
        val stmts = ("class Math {" +
                "fn add(x,y) { return x+y;}" +
                "fn mul(x,y) { return x*y;}" +
                "fn chain(x,y,z) { return mul(x, add(z,y));}" +
                "}" +
                " val m = Math();" +
                "m.chain(1,2,3);").tokenize().parseStmts()
        val expected = 5.0
        val actual = repl.eval(stmts)
        assert(actual == expected) { "actual = $actual, expected = $expected" }
    }

    @Test
    fun testInstanceCreationWithinDifferentClass() {
        val stmts = ("class Foo(value){} class Bar{" +
                "fn create_a_foo(v) {" +
                "return Foo(v);" +
                "} }" +
                "val b = Bar();" +
                "val f = b.create_a_foo(99);" +
                "f.value;").tokenize().parseStmts()
        val expected = 99.0
        val actual = repl.eval(stmts)
        assert(actual == expected) { "actual = $actual, expected = $expected" }
    }

    @Test
    fun testListSum() {
        val stmts = ("val l = list(); " +
                "l.add(10);" +
                "l.add(2);" +
                "var start = 0;" +
                "var sum  = 0;" +
                "while (start < 2) {" +
                "sum = sum + l.get(start);" +
                "start = start+1;" +
                "}" +
                "sum;").tokenize().parseStmts()
        val expected = repl.eval(stmts)
        val actual = 12.0
        assert(actual == expected) { "actual = $actual, expected = $expected" }

    }

    @Test
    fun testGlobalVarAccessFromWithinClass() {
        val stmts = ("@global val g = 10; class Foo{ fn get_global() {return g;} }" +
                "val thing = Foo();" +
                "thing.get_global();" +
                "").tokenize().parseStmts()
        val actual = repl.eval(stmts)
        val expected = 10.0
        assert(actual == expected) { "actual = $actual, expected = $expected" }
    }

    @Test
    fun testListModificationFromWithinClass() {
        val stmts = ("@global val lst = list(); " +
                "class Foo() { fn add(x) { lst.add(x); }}" +
                "val f = Foo();" + "" +
                "f.add(11);" +
                "lst.get(0);").tokenize().parseStmts()
        val actual = repl.eval(stmts)
        val expected = 11.0
        assert(actual == expected) { "actual = $actual, expected = $expected" }
    }

    @Test
    fun testCustomMathClass() {
        val stmts = ("class Math { fn add(x,y) { return x+y;}" +
                "fn abs(x) {return -x;}}" +
                "val m = Math();" +
                "val foo = -10;" +
                "val result = m.abs(foo);" +
                "result;").tokenize().parseStmts()
        val expected = 10.0
        val actual = repl.eval(stmts)
        assert(actual == expected) { "actual = $actual, expected = $expected" }
    }

    @Test
    fun testInvalidMethodOnList() {
        val stmts = "val lst = list(); l.ad(2);".tokenize().parseStmts()
        var correctAssertRaised = false
        try {
            repl.eval(stmts)
        } catch (e: VariableNotInScope) {
            correctAssertRaised = true
        }
        assert(correctAssertRaised, { "assertion not raised when caling invalid method" })
    }

    @Test
    fun testClosureVisibility() {
        val stmts = "fn foo(){  fn bar() { return 1;}  return bar();} foo();".tokenize().parseStmts()
        val actual = repl.eval(stmts)
        val expected = 1.0
        assert(actual == expected) { "actual = $actual, expected = $expected" }
    }

    @Test
    fun testMutationOfClassWithinClass() {
        val stmts =
            ("class Foo(x) {} class Bar(a_foo) {" +
                    "fn update(value) {a_foo.x = value;} " +
                    "fn get_foo_value() { " + "return a_foo.x;" + "}" +
                    " }" +
                    "val f = Foo(20);" +
                    "val bar = Bar(f);" +
                    "bar.update(99);" +
                    "bar.get_foo_value();").tokenize().parseStmts()
        val actual = repl.eval(stmts)
        val expected = 99.0
        assert(actual == expected) { "actual = $actual, expected = $expected" }
    }

    @Test
    fun testListEquality() {
        val stmts = "val a = list(); a.add(1); val b=list(); val c = list(); b.add(1); a  ==  b && a!=c;".tokenize()
            .parseStmts()
        val expected = repl.eval(stmts)
        val actual = true
        assert(actual == expected) { "actual = $actual, expected = $expected" }
    }

    @Test
    fun testGlobalUpdates() {
        val stmts = ("@global val global_list = list(); fn add_to_g_list() { global_list.add(444);} " +
                "add_to_g_list();" +
                "global_list.get(0);").tokenize().parseStmts()
        val expected = repl.eval(stmts)
        val actual = 444.0
        assert(actual == expected) { "actual = $actual, expected = $expected" }
    }

    @Test
    fun testVarNameAmbiguity() {
        val stmts = ("class Foo(value) { fn modify(_value) { this.value =_value;} }" +
                "val f = Foo(10);" +
                "f.modify(20);" +
                "f.value; ").tokenize().parseStmts()
        val actual = repl.eval(stmts)
        val expected = 20.0
        assert(actual == expected) { "actual = $actual, expected = $expected" }
    }

    @Test
    fun testMutationOnSharedRef() {
        val stmts = ("class MyList(lst) {}" +
                "val lst = list();" + "" +
                "val m = MyList(lst);" +
                "lst.add(999);" +
                "val c = m.lst.get(0);" +
                "c;").tokenize().parseStmts()
        val actual = repl.eval(stmts)
        val expected = 999.0
        assert(actual == expected) { "actual = $actual, expected = $expected" }
    }


}

