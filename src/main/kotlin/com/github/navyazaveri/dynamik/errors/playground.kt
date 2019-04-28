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

fun main(args: Array<String>) {
    val f = Foo()
    get_thing(f)
}