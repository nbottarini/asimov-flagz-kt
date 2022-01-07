package com.nbottarini.asimov.flagz.activations

import com.nbottarini.asimov.flagz.repositories.FeatureState
import com.nbottarini.asimov.flagz.user.FeatureUser

class UsersActivationStrategy: ActivationStrategy {
    override val id = ID

    override fun isEnabled(featureState: FeatureState, user: FeatureUser?): Boolean {
        if (user == null) return false
        val enabledUsers = getEnabledUsers(featureState)
        if (enabledUsers.isEmpty()) return false
        return enabledUsers.any { user.name.trim().lowercase() == it }
    }

    private fun getEnabledUsers(featureState: FeatureState) =
        featureState.strategyParams[PARAM_USERS]
            ?.split(",")
            ?.map { it.trim().lowercase() }
            ?: listOf()

    companion object {
        const val ID = "users"
        const val PARAM_USERS = "users"
    }
}
