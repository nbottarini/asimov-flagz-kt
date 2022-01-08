package com.nbottarini.asimov.flagz.manager

import com.nbottarini.asimov.flagz.Feature
import com.nbottarini.asimov.flagz.user.provider.UserProvider

interface FlagzManager {
    val userProvider: UserProvider

    fun allFeatures(): List<Feature>
    fun allEnabled(): List<Feature>
    fun enable(feature: Feature)
    fun disable(feature: Feature)
    fun isEnabled(feature: Feature): Boolean
}
