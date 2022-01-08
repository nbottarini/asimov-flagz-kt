package com.nbottarini.asimov.flagz.conditionalStrategies.rollingRelease

class DefaultStringHashCodeGenerator: StringHashCodeGenerator {
    override fun calculateFor(value: String) = value.hashCode()
}
