fun main(args: Array<String>) {

    val m = mutableMapOf<String, Int>()
    val thing: Int by m
    val wha: String by lazy {
        println("computing")
        "fopwjfe"
    }

    val foo: Int by lazy {
        println("eeke ekek")
        20
    }
    val raw = foo
}