package com.nbottarini.asimov.flagz.manager

import com.nbottarini.asimov.flagz.Feature
import com.nbottarini.asimov.flagz.activations.ActivationStrategy
import com.nbottarini.asimov.flagz.activations.ReleaseDateActivationStrategy
import com.nbottarini.asimov.flagz.activations.UsersActivationStrategy
import com.nbottarini.asimov.flagz.metadata.MetadataCache
import com.nbottarini.asimov.flagz.repositories.FeatureRepository
import com.nbottarini.asimov.flagz.repositories.FeatureState
import com.nbottarini.asimov.flagz.user.NullUserProvider
import com.nbottarini.asimov.flagz.user.UserProvider

class DefaultFlagzManager(
    featureEnums: List<Class<out Feature>>,
    private val repository: FeatureRepository,
    override val userProvider: UserProvider = NullUserProvider(),
): FlagzManager {
    private val metadataCache = MetadataCache(featureEnums)
    private val activationStrategies = mutableListOf(
        UsersActivationStrategy(),
        ReleaseDateActivationStrategy()
    )

    constructor(
        featureEnum: Class<out Feature>,
        repository: FeatureRepository,
        userProvider: UserProvider = NullUserProvider(),
    ): this(listOf(featureEnum), repository, userProvider)

    fun addActivationStrategy(strategy: ActivationStrategy) {
        activationStrategies.add(strategy)
    }

    override fun allFeatures() = metadataCache.allFeatures()

    override fun allEnabled(): List<Feature> {
        val allFeatures = allFeatures()
        val states = repository.get(allFeatures)
        return allFeatures.filter { isEnabled(it, states) }
    }

    override fun enable(feature: Feature) {
        val state = repository.get(feature) ?: defaultState(feature)
        repository.set(state.enabled())
    }

    override fun disable(feature: Feature) {
        val state = repository.get(feature) ?: defaultState(feature)
        repository.set(state.disabled())
    }

    override fun isEnabled(feature: Feature) = isEnabled(feature, listOfNotNull(repository.get(feature)))

    private fun isEnabled(feature: Feature, states: List<FeatureState>): Boolean {
        val state = states.firstOrNull { it.feature == feature } ?: defaultState(feature)
        if (state.isEnabled) {
            if (!state.hasStrategy()) return true

            val strategy = activationStrategies.firstOrNull { it.id == state.strategyId }
            return strategy?.isEnabled(state, userProvider.currentUser) ?: false
        }
        return false
    }

    private fun defaultState(feature: Feature) = metadataCache.get(feature).defaultState()
}
