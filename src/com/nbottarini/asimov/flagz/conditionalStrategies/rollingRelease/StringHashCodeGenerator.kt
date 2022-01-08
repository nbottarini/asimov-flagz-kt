package com.nbottarini.asimov.flagz.conditionalStrategies.rollingRelease

interface StringHashCodeGenerator {
    fun calculateFor(value: String): Int
}
