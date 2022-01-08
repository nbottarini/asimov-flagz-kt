package com.nbottarini.asimov.flagz.conditionalStrategies

import com.nbottarini.asimov.flagz.repositories.FeatureState
import com.nbottarini.asimov.flagz.user.FeatureUser

class UserAttributeStrategy: ConditionalStrategy {
    override val id = ID

    override fun isEnabled(featureState: FeatureState, user: FeatureUser?): Boolean {
        if (user == null) return false
        val attributeName = featureState.strategyParams[PARAM_NAME] ?: return false
        if (attributeName.isBlank()) return false
        val attributeValue = featureState.strategyParams[PARAM_VALUE]
        val userAttribute = user.getAttribute(attributeName) ?: ""
        return userAttribute == attributeValue
    }

    companion object {
        const val ID = "user-attribute"
        const val PARAM_NAME = "name"
        const val PARAM_VALUE = "value"
    }
}
