package com.nbottarini.asimov.flagz.activations

import com.nbottarini.asimov.flagz.repositories.FeatureState
import com.nbottarini.asimov.flagz.user.FeatureUser

interface ActivationStrategy {
    val id: String

    fun isEnabled(featureState: FeatureState, user: FeatureUser?): Boolean
}
