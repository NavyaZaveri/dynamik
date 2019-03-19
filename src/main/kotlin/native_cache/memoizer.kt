package native_cache

class Memoizer<K, T, V> {
    val map = mutableMapOf<T, V>()

    fun curry(f: K.(T) -> V): K.(T) -> V {
        return { x ->
            map.computeIfAbsent(x) { f(x) }
            map[x]!!
        }
    }
}

typealias  FuncName = String
typealias  Arg = Any
typealias Result = Any

class Blah {
    fun thing(funcStuff: Pair<FuncName, List<Arg>>): Any {
        println("yello");
        return 20
    }

    fun run() {

        val func = Blah::thing.memoize()
        var res = func(Pair("aa", mutableListOf()))
        res = func(Pair("aa", mutableListOf()))
    }

    fun <K, T, V> (K.(T) -> V).memoize(): K.(T) -> V {
        return Memoizer<K, T, V>().curry(this)
    }

}


fun stuff(n: Int): Int {
    println("some sutff")
    return 2;
}

fun main(args: Array<String>) {
    val b = Blah()
    b.run()

}
