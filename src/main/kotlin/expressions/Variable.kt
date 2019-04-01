package expressions

enum class VariableStatus {
    VAL,
    VAR
}

data class Variable(var value: Any, val status: VariableStatus)