
import com.github.navyazaveri.dynamik.errors.VariableNotInScope
import java.util.*


interface Generic<T> {

}

class Concrete_A : Generic<Int> {}
class Concrete_B : Generic<String> {}
class Concrete_c : Generic<String> {}

fun foo(thing: List<Any>) {
    thing.map { it as Generic<*> }
}


fun main(args: Array<String>) {
    throw VariableNotInScope("x", setOf("list"))
}

class Solution {
    fun searchMatrix(matrix: Array<IntArray>, target: Int): Boolean {
        return matrix.any { Arrays.binarySearch(it, target) >= 0 }
    }
}