package com.nbottarini.asimov.flagz.repositories.inMemory

import com.nbottarini.asimov.flagz.Feature
import com.nbottarini.asimov.flagz.repositories.FeatureRepository
import com.nbottarini.asimov.flagz.repositories.FeatureState
import java.util.concurrent.ConcurrentHashMap

class InMemoryFeatureRepository: FeatureRepository {
    private val features = ConcurrentHashMap<Feature, FeatureState>()

    override fun get(feature: Feature) = features[feature]

    override fun get(features: List<Feature>) = features.mapNotNull { this.features[it] }

    override fun set(state: FeatureState) {
        features[state.feature] = state
    }
}
