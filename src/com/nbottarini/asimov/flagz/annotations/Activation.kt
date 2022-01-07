package com.nbottarini.asimov.flagz.annotations

@Target(AnnotationTarget.FIELD)
annotation class Activation(val id: String, val parameters: Array<ActivationParam> = [])
