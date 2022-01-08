package com.nbottarini.asimov.flagz.repositories

import com.nbottarini.asimov.flagz.Feature

class FeatureState(
    val feature: Feature,
    val isEnabled: Boolean,
    val strategyId: String? = null,
    strategyParams: Map<String, String>? = null,
) {
    val strategyParams = strategyParams ?: mapOf()

    fun enabled() = FeatureState(feature, true, strategyId, strategyParams)

    fun disabled() = FeatureState(feature, false, strategyId, strategyParams)

    fun withParam(name: String, value: String): FeatureState {
        val params = strategyParams.toMutableMap()
        params[name] = value
        return FeatureState(feature, isEnabled, strategyId, params)
    }

    override fun equals(other: Any?) = other is FeatureState && other.feature == feature

    override fun hashCode() = feature.hashCode()
}
