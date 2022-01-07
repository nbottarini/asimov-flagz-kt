package com.nbottarini.asimov.flagz.repositories.environment

import com.nbottarini.asimov.flagz.Feature
import com.nbottarini.asimov.flagz.repositories.FeatureRepository
import com.nbottarini.asimov.flagz.repositories.FeatureState

class EnvironmentFeatureRepository(private val env: EnvironmentProvider = AsimovEnvironmentProvider()): FeatureRepository {
    private val prefix = "FEATURE_"

    override fun get(feature: Feature): FeatureState? {
        val name = prefix + feature.name.uppercase()
        return toFeatureState(feature, env.get(name))
    }

    override fun get(features: List<Feature>) = features.mapNotNull { get(it) }

    private fun toFeatureState(feature: Feature, value: String?): FeatureState? {
        if (value == null) return null
        val normalized = value.trim().lowercase()
        return FeatureState(feature, normalized == "true" || normalized == "1")
    }

    override fun set(state: FeatureState) {
        // Do nothing
    }
}
