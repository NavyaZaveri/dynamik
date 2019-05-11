import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
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


fun main(args: Array<String>) {
    val f = Foo();
    do_foo_hello(f);
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
