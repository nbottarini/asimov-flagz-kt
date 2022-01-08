package com.nbottarini.asimov.flagz.conditionalStrategies

class DefaultStringHashCodeGenerator: StringHashCodeGenerator {
    override fun calculateFor(value: String) = value.hashCode()
}
