import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.xml.bind.JAXBElement

interface Thing {
    fun do_stuff()
}

class Foo : Thing {
    override fun do_stuff() {
        println("doing foo")
    }
}

fun get_thing(t: Thing) {
    println("using generic thing")
}

fun get_thing(f: Foo) {
    println("using concrete foo")
}

fun hello() {
    for (i in 0..100000) {
        println("hello")
    }
}

suspend fun main(args: Array<String>) {
    runBlocking {
        val job = GlobalScope.launch { hello() }
        job.join()
    }
    print("done")

}
