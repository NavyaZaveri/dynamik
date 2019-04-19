package com.github.navyazaveri.dynamik.errors

import com.github.navyazaveri.dynamik.interpreter.levenshtein

class UnexpectedType(message: String) : Exception(message)
class VariableNotInScope(varName: String, candidates: Set<String>) :
    Exception("${varName} does not exist in the current scope, did you mean ${candidates.maxBy {
        levenshtein(
            it,
            varName
        )
    }}")

class ValError(varName: String) : Exception("${varName} is val, cannot reassign.")
class InvalidToken(message: String) : Exception(message)
class InvalidArgSize(actual: Int, expected: Int, fname: String) :
    Exception("passed $actual args to $fname, expected $expected")

class AssertionError(v1: Any, v2: Any) : Exception("Assertion Error: $v1 != $v2 ")
