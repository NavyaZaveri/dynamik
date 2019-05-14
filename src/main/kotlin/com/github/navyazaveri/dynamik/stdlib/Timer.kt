package com.github.navyazaveri.dynamik.stdlib

fun clockCallable(): NativeCallable<Double> {
    return NativeCallable("clock", 0) {
        System.currentTimeMillis().toDouble()
    }
}