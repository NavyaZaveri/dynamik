package interpreter

import java.lang.RuntimeException

enum class VariableStatus {
    VAL,
    VAR
}


data class Variable(var value: Any, val status: VariableStatus)

class Environment {
    val idenitifierToValue = mutableMapOf<String, Variable>()
    fun define(name: String, value: Any, status: VariableStatus) {
        if (exists(name)) {
            throw RuntimeException("cannot redefine $name")
        }
        idenitifierToValue[name] = Variable(status = status, value = value)
    }

    fun assign(name: String, value: Any) {
        if (!exists(name)) {
            throw RuntimeException("$name  is not defined in the current scope")
        }
        if (status(name) == VariableStatus.VAL) {
            throw RuntimeException("$name is a Val, cannot reassign")
        }
        idenitifierToValue[name] = Variable(value, VariableStatus.VAR)
    }

    fun exists(name: String): Boolean {
        return idenitifierToValue.containsKey(name)
    }

    fun status(name: String): VariableStatus = idenitifierToValue[name]?.status
        ?: throw RuntimeException("$name does not exist in the current scope")

    fun get(name: String): Any =
        idenitifierToValue[name]?.value ?: throw RuntimeException("$name not found in current environment")
}