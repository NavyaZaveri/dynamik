package com.github.navyazaveri.dynamik.interpreter

import com.github.navyazaveri.dynamik.errors.ValError
import com.github.navyazaveri.dynamik.errors.VariableNotInScope
import com.github.navyazaveri.dynamik.expressions.*

class Environment(private val identifierToValue: MutableMap<String, Variable> = mutableMapOf(), val name: String = "") {

    fun String.inGlobalScope(): Boolean {
        return globals.containsKey(this)
    }

    fun String.inCurrentScope(): Boolean {
        return identifierToValue.containsKey(this)
    }

    fun define(name: String, value: Any, status: VariableStatus) {
        if (name.inCurrentScope()) {
            throw ValError(name)
        }
        identifierToValue[name] = Variable(status = status, value = value)
    }

    fun clone(): Environment {
        return Environment(identifierToValue.toMutableMap())
    }

    fun clear() {
        identifierToValue.clear()
        globals.clear()
    }

    fun globals(): Map<String, Variable> {
        // functions are global
        return identifierToValue.filter { (_, v) -> v.value is DynamikCallable || v.value is MemoizedCallable }
    }

    fun assign(name: String, value: Any) {
        if (!name.inCurrentScope()) {
            throw VariableNotInScope(name, this.identifierToValue.keys)
        }
        if (status(name) == VariableStatus.VAL) {
            throw ValError(name)
        }

        identifierToValue[name] = Variable(value, VariableStatus.VAR)
    }

    fun status(name: String): VariableStatus = identifierToValue[name]?.status
        ?: throw VariableNotInScope(name, this.identifierToValue.keys)

    fun get(name: String): Any {
        if (name.inCurrentScope()) {
            return identifierToValue[name]!!.value
        }
        if (name.inGlobalScope()) {
            return globals[name]!!.value
        }

        val allExistingVars = identifierToValue.keys + globals.keys

        throw VariableNotInScope(name, allExistingVars)
    }

    companion object {
        val globals = mutableMapOf<String, Variable>()

        fun addGlobal(name: String, value: Any) {
            globals[name] = Variable(value, VariableStatus.VAL)
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
