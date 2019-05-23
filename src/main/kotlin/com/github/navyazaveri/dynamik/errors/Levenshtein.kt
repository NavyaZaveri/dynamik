package com.github.navyazaveri.dynamik.errors


/**
 * Tries to guess the mistyped variable/method based on [levenshtein] distance.
 */
fun closestMatch(name: String, candidates: Set<String>): String {
    return candidates.minBy { levenshtein(it, name) } ?: "None"
}

fun levenshtein(lhs: CharSequence, rhs: CharSequence): Int {
    val lhsLength = lhs.length
    val rhsLength = rhs.length

    var cost = IntArray(lhsLength + 1) { it }
    var newCost = IntArray(lhsLength + 1) { 0 }

    for (i in 1..rhsLength) {
        newCost[0] = i

        for (j in 1..lhsLength) {
            val editCost = if (lhs[j - 1] == rhs[i - 1]) 0 else 1

            val costReplace = cost[j - 1] + editCost
            val costInsert = cost[j] + 1
            val costDelete = newCost[j - 1] + 1

            newCost[j] = minOf(costInsert, costDelete, costReplace)
        }

        val swap = cost
        cost = newCost
        newCost = swap
    }
    return cost[lhsLength]
}