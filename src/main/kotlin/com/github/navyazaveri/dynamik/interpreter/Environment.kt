package com.github.navyazaveri.dynamik.interpreter

import com.github.navyazaveri.dynamik.errors.CallableDoesNotExist
import com.github.navyazaveri.dynamik.errors.ValError
import com.github.navyazaveri.dynamik.errors.VariableNotInScope
import com.github.navyazaveri.dynamik.expressions.*

/**
 *An environment instance held by the interpreter
 *@property name name of the environment
 *@property identifierToValue captures the value of a given variable
 *@Constructor Creates an environment
 */

class Environment(val identifierToValue: MutableMap<String, ValueWrapper<Any>> = mutableMapOf(), val name: String = "") {
    val fields = mutableMapOf<String, ValueWrapper<Any>>()
    val classes = mutableMapOf<String, ValueWrapper<DynamikClass<out DynamikInstance>>>()
    val functions = mutableMapOf<String, ValueWrapper<DynamikFunction<out Any>>>()
    var outer = mutableMapOf<String, ValueWrapper<Any>>()


    fun String.inGlobalScope(): Boolean {
        return globals.containsKey(this)
    }

    fun String.inCurrentScope(): Boolean {
        return identifierToValue.containsKey(this)
    }

    /**
     * Assigns [value] to [name]
     * @throws ValError when variable has been previously defined with <code> val </code>
     */
    fun define(name: String, value: Any, status: VariableStatus, type: VarType = VarType.IDENT) {
        if (name.inCurrentScope() && identifierToValue[name]!!.status == VariableStatus.VAL) {
            throw ValError(name)
        }
        identifierToValue[name] = ValueWrapper(status = status, value = value, type = type)
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

    fun functions(): Map<String, ValueWrapper<DynamikFunction<out Any>>> {
        return functions
    }

    fun fields(): Map<String, ValueWrapper<Any>> {
        return fields
    }

    fun clone(): Environment {
        return Environment(this.identifierToValue.toMutableMap())
    }

    fun classes(): Map<String, ValueWrapper<DynamikClass<out DynamikInstance>>> {
        return classes
    }

    fun assign(name: String, value: Any) {
        if (!name.inCurrentScope()) {
            throw VariableNotInScope(name, this.identifierToValue.keys)
        }
        if (status(name) == VariableStatus.VAL) {
            throw ValError(name)
        }

        identifierToValue[name] = ValueWrapper(value, VariableStatus.VAR)
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
        throw CallableDoesNotExist(name, this.identifierToValue.keys)
    }


    /**
     * @throws VariableNotInScope
     * @throws ValError
     */
    fun defineField(name: String, value: Any) {
        fields[name] = ValueWrapper(value, VariableStatus.VAR)
        define(name, value, VariableStatus.VAR)
    }

    fun defineClass(name: String, value: DynamikClass<out DynamikInstance>, global: Boolean = false) {
        classes[name] = ValueWrapper(value, VariableStatus.VAL)
        define(name, value, VariableStatus.VAL, type = VarType.CLASS)
        if (global) {
            addGlobal(name, value)
        }
    }

    fun defineFunction(name: String, value: DynamikFunction<out Any>, global: Boolean = false) {
        functions[name] = ValueWrapper(value, VariableStatus.VAL)
        define(name, value, VariableStatus.VAL, type = VarType.FN)
        if (global) {
            addGlobal(name, value)
        }
    }

    companion object {
        val globals = mutableMapOf<String, ValueWrapper<Any>>()
        fun addGlobal(name: String, value: Any) {
            globals[name] = ValueWrapper(value, VariableStatus.VAL)
        }
    }

    override fun toString(): String {
        return identifierToValue.toString()
    }
}

