import com.github.navyazaveri.dynamik.scanner.TokenType
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.lang.RuntimeException
import kotlin.reflect.KClass

interface HelloBoy {
    fun hello()
}

interface Built


class Foo {

    fun add(a: Any, b: Any) {
        println("adding things");
    }
}


fun do_foo_hello(f: Foo) {
    val lst: List<Any> = listOf(1, 2);
    val argTypes = (0 until 2).map { Any::class.java }.toTypedArray()
    val a = f::class.java.getMethod("add", *argTypes).invoke(f, *lst.toTypedArray())
}

fun get_num(a: TokenType): Int {
    when (a) {
        TokenType.MINUS -> return -1
        TokenType.PRINT -> {
            println("hello")
            if (100 == 100) {
                return 0
            }
        }
        else -> return 1
    }
    return 20;

}


fun main(args: Array<String>) {
    val f = Foo();
    get_num(TokenType.CLASS)

}

fun foo(thing: Generic_A<out Generic_B>) {

}

abstract class Generic_B {

}

interface Generic_A<Generic_B> {

}

class Concrete_B : Generic_B() {

}

class Concrete_A : Generic_A<Concrete_B> {

}
