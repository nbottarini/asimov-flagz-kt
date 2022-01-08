package com.nbottarini.asimov.flagz.conditionalStrategies

import com.nbottarini.asimov.flagz.repositories.FeatureState
import com.nbottarini.asimov.flagz.user.FeatureUser

interface ConditionalStrategy {
    val id: String

    fun isEnabled(featureState: FeatureState, user: FeatureUser?): Boolean
}
