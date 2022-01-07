package com.nbottarini.asimov.flagz.user

class NullUserProvider: UserProvider {
    override val currentUser: FeatureUser? = null
}
