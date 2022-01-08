package com.nbottarini.asimov.flagz.conditionalStrategies.annotations

@Target(AnnotationTarget.FIELD)
annotation class Conditional(val id: String, val parameters: Array<Param> = [])
