package interpreter

import errors.VariableNotInScope
import expressions.Callable
import expressions.Variable
import expressions.VariableStatus
import java.lang.RuntimeException

class Environment(val identifierToValue: MutableMap<String, Variable> = mutableMapOf()) {

    fun define(name: String, value: Any, status: VariableStatus) {
        if (exists(name)) {
            throw RuntimeException("cannot redefine $name")
        }
        identifierToValue[name] = Variable(status = status, value = value)
    }

    fun globals(): Map<String, Variable> {
        // functions are global
        return identifierToValue.filter { (k, v) -> v.value is Callable }
    }

    fun assign(name: String, value: Any) {
        if (!exists(name)) {
            throw RuntimeException("$name  is not defined in the current scope.")
        }
        if (status(name) == VariableStatus.VAL) {
            throw RuntimeException("$name is a Val, cannot reassign.")
        }

        identifierToValue[name] = Variable(value, VariableStatus.VAR)
    }

    fun exists(name: String): Boolean = identifierToValue.containsKey(name)

    fun status(name: String): VariableStatus = identifierToValue[name]?.status
        ?: throw VariableNotInScope(
            "$name does not exist in the current scope, did you mean ${cloestMatch(
                name,
                identifierToValue.keys
            )}?"
        )

    fun get(name: String): Any {
        return identifierToValue[name]?.value ?: throw VariableNotInScope(
            "$name does not exist, did you mean ${cloestMatch(
                name,
                identifierToValue.keys
            )}"
        )
    }

    companion object {
        fun cloestMatch(unknown: String, candidates: Set<String>): String {
            return candidates.sortedBy { levenshtein(it, unknown) }[0]
        }
    }
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
