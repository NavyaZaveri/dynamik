package com.github.navyazaveri.dynamik.expressions


/**
 * Captures a returned [value] with a RuntimeException
 */
class Return(val value: Any) : RuntimeException()
