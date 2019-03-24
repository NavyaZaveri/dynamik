package interpreter

import expressions.Callable
import expressions.Variable
import expressions.VariableStatus
import java.lang.RuntimeException
import java.util.Map


class Environment(val identifierToValue: MutableMap<String, Variable> = mutableMapOf()) {

    fun define(name: String, value: Any, status: VariableStatus) {
        if (exists(name)) {
            throw RuntimeException("cannot redefine $name")
        }
        identifierToValue[name] = Variable(status = status, value = value)
    }

    fun globals(): kotlin.collections.Map<String, Variable> {
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
        ?: throw RuntimeException("$name does not exist in the current scope")

    fun get(name: String): Any =
        identifierToValue[name]?.value ?: throw RuntimeException("$name not found in current environment")

}


