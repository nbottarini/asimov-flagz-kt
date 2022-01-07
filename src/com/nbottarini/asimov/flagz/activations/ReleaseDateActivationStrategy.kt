package com.nbottarini.asimov.flagz.activations

import com.nbottarini.asimov.time.Clock
import com.nbottarini.asimov.time.LocalDateTimeParser
import com.nbottarini.asimov.flagz.repositories.FeatureState
import com.nbottarini.asimov.flagz.user.FeatureUser

class ReleaseDateActivationStrategy: ActivationStrategy {
    override val id = ID

    override fun isEnabled(featureState: FeatureState, user: FeatureUser?): Boolean {
        val dateStr = featureState.strategyParams[PARAM_DATE] ?: return false
        if (dateStr.isBlank()) return false

        val date = LocalDateTimeParser().parseISO8601(dateStr)
        return Clock.now() >= date
    }

    companion object {
        const val ID = "release-date"
        const val PARAM_DATE = "date"
    }
}
