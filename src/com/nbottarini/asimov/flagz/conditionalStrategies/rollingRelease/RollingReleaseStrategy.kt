package com.nbottarini.asimov.flagz.conditionalStrategies.rollingRelease

import com.nbottarini.asimov.flagz.conditionalStrategies.ConditionalStrategy
import com.nbottarini.asimov.flagz.repositories.FeatureState
import com.nbottarini.asimov.flagz.user.FeatureUser

class RollingReleaseStrategy(private val hashcodeGenerator: StringHashCodeGenerator): ConditionalStrategy {
    override val id = ID

    override fun isEnabled(featureState: FeatureState, user: FeatureUser?): Boolean {
        if (user == null || user.name.isBlank()) return false
        val percentage = getPercentage(featureState) ?: return false
        val hashCode = getUserHashcode(user, featureState)
        return (hashCode % 100) < percentage
    }

    private fun getPercentage(featureState: FeatureState): Int? {
        val percentage = featureState.strategyParams[PARAM_PERCENTAGE]?.toIntOrNull() ?: return null
        if (percentage <= 0) return null
        return percentage
    }

    private fun getUserHashcode(user: FeatureUser, featureState: FeatureState): Int {
        val userName = user.name.lowercase().trim()
        val featureName = featureState.feature.name
        return hashcodeGenerator.calculateFor("$userName:$featureName")
    }

    companion object {
        const val ID = "rolling-release"
        const val PARAM_PERCENTAGE = "percentage"
    }
}
