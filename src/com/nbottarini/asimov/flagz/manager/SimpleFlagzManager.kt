package com.nbottarini.asimov.flagz.manager

import com.nbottarini.asimov.flagz.Feature
import com.nbottarini.asimov.flagz.manager.metadata.defaultState
import com.nbottarini.asimov.flagz.repositories.inMemory.InMemoryFeatureRepository
import com.nbottarini.asimov.flagz.user.provider.ThreadLocalUserProvider

class SimpleFlagzManager: FlagzManager {
    private val repository = InMemoryFeatureRepository()
    override val userProvider = ThreadLocalUserProvider()

    override fun allFeatures(): List<Feature> = listOf()

    override fun allEnabled(): List<Feature> = listOf()

    override fun enable(feature: Feature) {
        val state = repository.get(feature) ?: feature.defaultState()
        repository.set(state.enabled())
    }

    override fun disable(feature: Feature) {
        val state = repository.get(feature) ?: feature.defaultState()
        repository.set(state.disabled())
    }

    override fun isEnabled(feature: Feature): Boolean {
        val state = repository.get(feature) ?: feature.defaultState()
        return state.isEnabled
    }
}
