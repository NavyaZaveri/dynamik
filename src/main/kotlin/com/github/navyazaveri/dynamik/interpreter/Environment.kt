package com.github.navyazaveri.dynamik.interpreter

import com.github.navyazaveri.dynamik.errors.ValError
import com.github.navyazaveri.dynamik.errors.VariableNotInScope
import com.github.navyazaveri.dynamik.expressions.*

/**
 *An environment instance held by the interpreter
 *@property name name of the environment
 *@property identifierToValue captures the value of a given variable
 *@Constructor Creates an environment
 */

class Environment(val identifierToValue: MutableMap<String, Variable<Any>> = mutableMapOf(), val name: String = "") {
    val fields = mutableMapOf<String, Variable<Any>>()
    val classes = mutableMapOf<String, Variable<DynamikClass<out DynamikInstance>>>()
    val functions = mutableMapOf<String, Variable<DynamikFunction<Any>>>()
    var outer = mutableMapOf<String, Variable<Any>>()


    fun String.inGlobalScope(): Boolean {
        return globals.containsKey(this)
    }

    fun String.inCurrentScope(): Boolean {
        return identifierToValue.containsKey(this)
    }

    /**
     * Assigns [value] to [name]
     *@throws ValError when variable has been previously defined with <code> val </code>
     */
    fun define(name: String, value: Any, status: VariableStatus, type: VarType = VarType.IDENT) {
        if (name.inCurrentScope() && identifierToValue[name]!!.status == VariableStatus.VAL) {
            throw ValError(name)
        }
        identifierToValue[name] = Variable(status = status, value = value, type = type)
    }


    /**
     * Clears all scopes.
     */
    fun clear() {
        identifierToValue.clear()
        classes.clear()
        fields.clear()
        globals.clear()
    }

    fun functions(): Map<String, Variable<DynamikFunction<Any>>> {
        return functions
    }

    fun fields(): Map<String, Variable<Any>> {
        return fields
    }

    fun clone(): Environment {
        return Environment(this.identifierToValue.toMutableMap())
    }

    fun classes(): Map<String, Variable<DynamikClass<out DynamikInstance>>> {
        return classes
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

    /**
     * @throws VariableNotInScope
     */
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

    /**
     * @throws VariableNotInScope
     */
    fun getCallable(name: String): Callable<*> {
        if (name in functions) {
            return functions[name]!!.value
        }
        if (name in classes) {
            return classes[name]!!.value
        }
        throw VariableNotInScope(name, this.identifierToValue.keys)
    }


    /**
     * @throws VariableNotInScope
     * @throws ValError
     */
    fun defineField(name: String, value: Any) {
        fields[name] = Variable(value, VariableStatus.VAR)
        define(name, value, VariableStatus.VAR)
    }

    fun defineClass(name: String, value: DynamikClass<out DynamikInstance>) {
        classes[name] = Variable(value, VariableStatus.VAL)
        define(name, value, VariableStatus.VAL, type = VarType.CLASS)
    }

    fun defineFunction(name: String, value: DynamikFunction<Any>) {
        functions[name] = Variable(value, VariableStatus.VAL)
        define(name, value, VariableStatus.VAL, type = VarType.FN)
    }


    companion object {
        val globals = mutableMapOf<String, Variable<Any>>()
        fun addGlobal(name: String, value: Any) {
            globals[name] = Variable(value, VariableStatus.VAL)
        }
    }

    override fun toString(): String {
        return identifierToValue.toString()
    }
}


/**
 * [levenshtein] finds the minimum edits required to convert [lhs] to [rhs]
 *
 */
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
