package com.nbottarini.asimov.flagz.user.provider

import com.nbottarini.asimov.flagz.user.FeatureUser

class NullUserProvider: UserProvider {
    override val currentUser: FeatureUser? = null
}
