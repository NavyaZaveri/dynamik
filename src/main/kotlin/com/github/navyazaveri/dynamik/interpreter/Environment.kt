package com.github.navyazaveri.dynamik.interpreter

import com.github.navyazaveri.dynamik.errors.CallableDoesNotExist
import com.github.navyazaveri.dynamik.errors.ValError
import com.github.navyazaveri.dynamik.errors.VariableNotInScope
import com.github.navyazaveri.dynamik.expressions.*


typealias Class = DynamikClass<out DynamikInstance>
typealias Func = DynamikFunction<out Any>


/**
 * An environment instance held by the interpreter
 * @property name name of the environment
 * @property identifierToValue captures the value of a given variable
 * @Constructor Creates an environment
 */

class Environment(
    val identifierToValue: MutableMap<String, ValueWrapper<Any>> = mutableMapOf(),
    val name: String = ""
) {
    val fields: MutableMap<String, ValueWrapper<Any>> by lazy {
        mutableMapOf<String, ValueWrapper<Any>>()
    }
    val classes: MutableMap<String, ValueWrapper<Class>> by lazy {
        mutableMapOf<String, ValueWrapper<Class>>()
    }
    val functions: MutableMap<String, ValueWrapper<Func>> by lazy {
        mutableMapOf<String, ValueWrapper<Func>>()
    }
    var outer = mutableMapOf<String, ValueWrapper<Any>>()

    private fun String.inGlobalScope(): Boolean {
        return globals.containsKey(this)
    }

    //TODO
    fun add(new: Environment) {
        this.classes.putAll(new.classes)
    }

    private fun String.inCurrentScope(): Boolean {
        return identifierToValue.containsKey(this)
    }


    /**
     * Assigns [value] to [name]
     * @throws ValError when variable h as been previously defined with <code> val </code>
     */
    fun define(name: String, value: Any, status: VariableStatus, type: VarType = VarType.IDENT) {
        val valWrapper = ValueWrapper(status = status, value = value, type = type)
        if (name in identifierToValue && (identifierToValue[name]!!.status == VariableStatus.VAL)) {
            throw ValError(name)
        }

        if (name in identifierToValue && identifierToValue[name]!!.type == VarType.CLASS_FIELD) {
            throw java.lang.RuntimeException("ambiguous")
        }

        identifierToValue[name] = valWrapper
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
        identifierToValue[name]!!.value = value
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
        if (name.inGlobalScope() && globalAccess) {
            return globals[name]!!.value
        }

        val allExistingVars = identifierToValue.keys + globals.keys

        throw VariableNotInScope(name, allExistingVars)
    }

    fun getField(name: String): Any {
        val fields = identifierToValue.filter { it.value.type == VarType.CLASS_FIELD }
        if (name in fields) {
            return fields[name]!!.value
        }
        throw RuntimeException("$name does not exist as a field in the current scope")
    }

    fun setField(name: String, value: Any) {
        val fields = identifierToValue.filter { it.value.type == VarType.CLASS_FIELD }
        if (name in fields) {
            fields[name]!!.value = value
        } else {
            throw java.lang.RuntimeException("$name does not exist")
        }
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
        val valWrapper = ValueWrapper(value, VariableStatus.VAR, type = VarType.CLASS_FIELD)
        if (name in identifierToValue && (identifierToValue[name]!!.status == VariableStatus.VAL)) {
            throw ValError(name)
        }
        if (name in identifierToValue && identifierToValue[name]!!.type == VarType.CLASS_FIELD) {
            throw java.lang.RuntimeException("ambiguous")
        }
        fields[name] = valWrapper
        identifierToValue[name] = valWrapper

        // define(name, valWrapper, VariableStatus.VAR, type = VarType.CLASS_FIELD)
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
        var globalAccess: Boolean = true
        val globals = mutableMapOf<String, ValueWrapper<Any>>()
        fun addGlobal(name: String, value: Any) {
            globals[name] = ValueWrapper(value, VariableStatus.VAL)
        }
    }

    override fun toString(): String {
        return identifierToValue.toString()
    }
}

