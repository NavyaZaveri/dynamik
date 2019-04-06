package errors

class UnexpectedType(message: String) : Exception(message)
class VariableNotInScope(message: String) : Exception(message)
class ValError(message: String = "cannot reassasign") : Exception(message)
class InvalidToken(message: String) : Exception(message)
