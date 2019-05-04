import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.reflect.KClass

interface HelloBoy {
    fun hello()
}

interface Built


class Foo(val m: MutableList<Any>) : MutableList<Any> by m, Built {
}


fun do_foo_hello(f: Foo) {

    val argTypes = (0 until 1).map { Any::class.java }.toTypedArray()
    val a = f::class.java.getMethod("add", *argTypes).invoke(f, 10)
    f::class.java.getMethod("size").invoke(f).also { println(it) }
}


fun main(args: Array<String>) {
    val a = Concrete_A();
    foo(a);

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
