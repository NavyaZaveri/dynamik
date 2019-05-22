import java.util.*

fun main(args: Array<String>) {

}

class Solution {
    fun searchMatrix(matrix: Array<IntArray>, target: Int): Boolean {
        return matrix.any { Arrays.binarySearch(it, target) >= 0 }
    }
}