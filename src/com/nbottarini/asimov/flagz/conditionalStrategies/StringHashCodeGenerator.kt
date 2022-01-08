package com.nbottarini.asimov.flagz.conditionalStrategies

interface StringHashCodeGenerator {
    fun calculateFor(value: String): Int
}
