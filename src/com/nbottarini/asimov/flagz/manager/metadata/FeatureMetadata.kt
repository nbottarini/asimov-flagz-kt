package com.nbottarini.asimov.flagz.manager.metadata

import com.nbottarini.asimov.flagz.Feature
import com.nbottarini.asimov.flagz.conditionalStrategies.Conditional
import com.nbottarini.asimov.flagz.EnabledByDefault
import com.nbottarini.asimov.flagz.repositories.FeatureState

data class FeatureMetadata(
    val feature: Feature,
    val isEnabledByDefault: Boolean,
    val strategyId: String? = null,
    val strategyParams: Map<String, String>? = null,
) {
    fun defaultState() = FeatureState(feature, isEnabledByDefault, strategyId, strategyParams)
}

fun Feature.defaultState() = metadata().defaultState()

fun Feature.metadata(): FeatureMetadata {
    val field = javaClass.getField(name)
    val isEnabledByDefault = field.isAnnotationPresent(EnabledByDefault::class.java)
    var strategyId: String? = null
    var strategyParams = mapOf<String, String>()
    val conditional = field.getAnnotation(Conditional::class.java)
    if (conditional != null) {
        strategyId = conditional.id
        strategyParams = conditional.parameters.associate { it.name to it.value }
    }
    return FeatureMetadata(this, isEnabledByDefault, strategyId, strategyParams)
}
