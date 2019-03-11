package interpreter

import java.lang.RuntimeException

class Environment {
    val idenitifierToValue = mutableMapOf<String, Any>()
    fun define(name: String, value: Any) {
        idenitifierToValue[name] = value
    }

    fun get(name: String): Any {
        return idenitifierToValue[name] ?: throw RuntimeException("$name not found in current environment")
    }
}