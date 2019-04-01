package native_cache

class Memoizer<C, K, V> {
    private val cache = mutableMapOf<K, V>()
    var hits = 0

    fun curry(f: C.(K) -> V): C.(K) -> V {
        return { x ->
            cache.computeIfAbsent(x) { f(x).also { hits -= 1 } }
            hits += 1
            cache[x]!!
        }
    }
}

fun <C, K, V> (C.(K) -> V).memoize(): C.(K) -> V {
    return Memoizer<C, K, V>().curry(this)
}

typealias FuncName = String
typealias Arg = Any
typealias Result = Any

class Blah {
    val func = Blah::thing.memoize()

    fun thing(funcStuff: Pair<FuncName, List<Arg>>) {
        println("helloe")
    }

    fun run(): Any {
        /*
        If the functionis nened to memo'd, 
        return func(), otherwise just invoke
        the callable i.e, thing  normally
         */
        return func(Pair("aa", mutableListOf()))
    }
}

fun main(args: Array<String>) {
    val b = Blah()
    b.run()
    b.run()
}
