package errors

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

fun compute(t: MutableList<Int>) {
    println("computing")
}

open class Animal {

    val name = "foo"
    fun bark() {
        println("barking")
    }
}


object blah : Animal() {
    val m = "off"
    fun hello() {
        println(this.name)
        this.bark()
    }
}

object Locker {
    suspend fun lock_this(stuff: () -> Unit) {
        Mutex().withLock { stuff }
    }
}

fun blah() {
    GlobalScope.launch {
        blah()
        Locker.lock_this { }
    }
}

fun do_a_lock() {
    GlobalScope.launch {
        blah()
        Locker.lock_this { }
    }
}

suspend fun stuff() {

}
