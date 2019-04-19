package com.github.navyazaveri.dynamik.interpreter

import com.github.navyazaveri.dynamik.errors.ValError
import com.github.navyazaveri.dynamik.errors.VariableNotInScope
import com.github.navyazaveri.dynamik.expressions.Callable
import com.github.navyazaveri.dynamik.expressions.Variable
import com.github.navyazaveri.dynamik.expressions.VariableStatus
import java.lang.RuntimeException

class Environment(val identifierToValue: MutableMap<String, Variable> = mutableMapOf()) {


    fun define(name: String, value: Any, status: VariableStatus) {
        if (existsInCurrentScope(name)) {
            throw ValError(name)
        }
        identifierToValue[name] = Variable(status = status, value = value)
    }

    fun clone(): Environment {
        return Environment(identifierToValue.toMutableMap())
    }

    fun globals(): Map<String, Variable> {
        // functions are global
        return identifierToValue.filter { (_, v) -> v.value is Callable }
    }

    fun assign(name: String, value: Any) {
        if (!existsInCurrentScope(name)) {
            throw VariableNotInScope(name, this.identifierToValue.keys)
        }
        if (status(name) == VariableStatus.VAL) {
            throw ValError(name)
        }

        identifierToValue[name] = Variable(value, VariableStatus.VAR)
    }

    fun existsInCurrentScope(name: String): Boolean = identifierToValue.containsKey(name)

    fun existsGlobally(name: String): Boolean = globals.containsKey(name)

    fun status(name: String): VariableStatus = identifierToValue[name]?.status
        ?: throw VariableNotInScope(name, this.identifierToValue.keys)

    fun get(name: String): Any {
        if (existsInCurrentScope(name)) {
            return identifierToValue[name]!!.value
        }
        if (existsGlobally(name)) {
            return globals[name]!!.value
        }

        val vars = identifierToValue.keys + globals.keys

        throw VariableNotInScope(name, this.identifierToValue.keys)
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
