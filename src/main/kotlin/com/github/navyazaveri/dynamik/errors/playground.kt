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
    do_foo_hello(Foo(mutableListOf()))
    val c: Wa<*> = Blah()

}

interface Wa<T : Any> {
    fun wut(): T
}

class Blah : Wa<Any> {
    override fun wut(): Any {
        return println("dwi")
    }
}
