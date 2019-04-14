package errors

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

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
    val m = "off";
    fun hello() {
        println(this.name)
        this.bark()
    }
}


fun main() {

    val thing = mutableListOf<Int>()
    GlobalScope.launch {
        blah.hello()
    }

    runBlocking {

    }
}