package com.github.navyazaveri.dynamik.errors

class UnexpectedType(message: String) : Exception(message)
class VariableNotInScope(message: String) : Exception(message)
class ValError(message: String = "cannot reassign") : Exception(message)
class InvalidToken(message: String) : Exception(message)
class InvalidArgumentSize(message: String) : Exception(message)

