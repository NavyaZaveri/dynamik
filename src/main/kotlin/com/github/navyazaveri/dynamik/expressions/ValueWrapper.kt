package com.github.navyazaveri.dynamik.expressions


/**
 * Determines if a variable is reassignable ([var]) or not ([val])
 */
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

/**
 * Encapsulates a variable in Dynamik
 */
data class ValueWrapper<T : Any>(var value: T, val status: VariableStatus, val type: VarType = VarType.IDENT)