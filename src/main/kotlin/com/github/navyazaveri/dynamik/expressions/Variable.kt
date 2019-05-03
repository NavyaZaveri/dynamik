package com.github.navyazaveri.dynamik.expressions


enum class VariableStatus {
    VAL,
    VAR
}

enum class VarType {
    CLASS,
    FN,
    IDENT,
    CLASS_FIELD
}

data class Variable(var value: Any, val status: VariableStatus, val type: VarType = VarType.IDENT)