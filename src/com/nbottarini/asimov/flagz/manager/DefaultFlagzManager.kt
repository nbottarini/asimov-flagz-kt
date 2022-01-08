package com.nbottarini.asimov.flagz.manager

import com.nbottarini.asimov.flagz.Feature
import com.nbottarini.asimov.flagz.conditionalStrategies.ConditionalStrategy
import com.nbottarini.asimov.flagz.conditionalStrategies.ReleaseDateStrategy
import com.nbottarini.asimov.flagz.conditionalStrategies.UsersStrategy
import com.nbottarini.asimov.flagz.manager.metadata.MetadataCache
import com.nbottarini.asimov.flagz.repositories.FeatureRepository
import com.nbottarini.asimov.flagz.repositories.FeatureState
import com.nbottarini.asimov.flagz.user.provider.NullUserProvider
import com.nbottarini.asimov.flagz.user.provider.UserProvider

class DefaultFlagzManager(
    featureEnums: List<Class<out Feature>>,
    private val repository: FeatureRepository,
    override val userProvider: UserProvider = NullUserProvider(),
): FlagzManager {
    private val metadataCache = MetadataCache(featureEnums)
    private val conditionalStrategies = mutableListOf(
        UsersStrategy(),
        ReleaseDateStrategy()
    )

    constructor(
        featureEnum: Class<out Feature>,
        repository: FeatureRepository,
        userProvider: UserProvider = NullUserProvider(),
    ): this(listOf(featureEnum), repository, userProvider)

    fun addConditionalStrategy(strategy: ConditionalStrategy) {
        conditionalStrategies.add(strategy)
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
        return state.isEnabled && isConditionalEnabled(state)
    }

    private fun isConditionalEnabled(state: FeatureState): Boolean {
        val strategy = conditionalStrategies.firstOrNull { it.id == state.strategyId } ?: return true
        return strategy.isEnabled(state, userProvider.currentUser)
    }

    private fun defaultState(feature: Feature) = metadataCache.get(feature).defaultState()
}
