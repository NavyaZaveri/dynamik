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

    val x = (0 until 1).map { Any::class.java }.toTypedArray()
    val a = f::class.java.getMethod("add", *x).invoke(f, 10)
    println(a)
}


fun main(args: Array<String>) {
    do_foo_hello(Foo(mutableListOf()))
}