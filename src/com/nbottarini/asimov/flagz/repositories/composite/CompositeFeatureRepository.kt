package com.nbottarini.asimov.flagz.repositories.composite

import com.nbottarini.asimov.flagz.Feature
import com.nbottarini.asimov.flagz.repositories.FeatureRepository
import com.nbottarini.asimov.flagz.repositories.FeatureState

class CompositeFeatureRepository(
    private val repositories: List<FeatureRepository>,
    private val setStrategy: SetStrategies = SetStrategies.FIRST,
): FeatureRepository {

    constructor(vararg repositories: FeatureRepository, setStrategy: SetStrategies = SetStrategies.FIRST)
            : this(repositories.toList(), setStrategy)

    override fun get(feature: Feature): FeatureState? {
        for (repository in repositories) {
            val state = repository.get(feature)
            if (state != null) return state
        }
        return null
    }

    override fun get(features: List<Feature>): List<FeatureState> {
        val featuresToProcess = features.toMutableList()
        val states = mutableListOf<FeatureState>()
        for (repository in repositories) {
            if (featuresToProcess.isEmpty()) return states
            val repositoryStates = repository.get(featuresToProcess)
            states.addAll(repositoryStates)
            featuresToProcess.removeAll(repositoryStates.map { it.feature })
        }
        return states
    }

    override fun set(state: FeatureState) {
        when(setStrategy) {
            SetStrategies.FIRST -> repositories.first().set(state)
            SetStrategies.LAST -> repositories.last().set(state)
            SetStrategies.ALL -> repositories.forEach { it.set(state) }
        }
    }

    enum class SetStrategies { FIRST, LAST, ALL }
}
