package com.github.navyazaveri.dynamik.errors


class VariableNotInScope(varName: String, candidates: Set<String>) :
    Exception("$varName does not exist, did you mean +${closestMatch(varName, candidates)}+")


class UnexpectedType(message: String) : Exception(message)

class ValError(varName: String) : Exception("$varName is val, cannot reassign.")
class InvalidToken(message: String) : Exception(message)
class InvalidArgSize(actual: Int, expected: Int, fname: String) :
    Exception("passed $actual args to $fname, expected $expected")

class AssertionErr(v1: Any) : Exception("Assertion Error")
class InvalidConstructorArgSize(actual: Int, expected: Int, fname: String) :
    Exception("Passed actual args to $fname, expected $expected")

class CallableDoesNotExist(varName: String, candidates: Set<String>) :
    Exception("$varName does not exist, did you mean +${closestMatch(varName, candidates)}+")



