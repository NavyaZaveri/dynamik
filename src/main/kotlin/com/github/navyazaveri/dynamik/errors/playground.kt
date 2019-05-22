import java.util.*

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

class Solution {
    fun searchMatrix(matrix: Array<IntArray>, target: Int): Boolean {
        return matrix.any { Arrays.binarySearch(it, target) >= 0 }
    }
}