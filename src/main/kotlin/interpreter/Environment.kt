package interpreter

import java.lang.RuntimeException

enum class VariableStatus {
    VAL,
    VAR
}


data class Variable(val status: VariableStatus, var value: Any)

class Environment {
    val idenitifierToValue = mutableMapOf<String, Variable>()
    fun define(name: String, value: Any, declaration: Boolean) {
        if (declaration) {
            if (exists(name)) {
                throw RuntimeException("$name already exists, cannot redeclare it")
            }
            if (status(name) == VariableStatus.VAL) {
                throw RuntimeException("cannot reassign val ")
            }
            //the variable exists for sure, and is a Var variable
            idenitifierToValue[name]!!.value = name
        } else {
            if (!exists(name)) {
                throw RuntimeException("$name does not exist in the current scope.")
            }
            if (status(name) == VariableStatus.VAL) {
                throw RuntimeException("cannot reassign val")
            }
            idenitifierToValue[name]!!.value = value
        }
    }

    fun exists(name: String): Boolean {
        return idenitifierToValue.containsKey(name)
    }

    fun status(name: String): VariableStatus {
        return idenitifierToValue[name]?.status
            ?: throw RuntimeException("$name does not exist in the current scope")
    }

    fun get(name: String): Any {
        return idenitifierToValue[name] ?: throw RuntimeException("$name not found in current environment")
    }
}