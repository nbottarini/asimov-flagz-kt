package com.nbottarini.asimov.flagz.user.provider

import com.nbottarini.asimov.flagz.user.FeatureUser

interface UserProvider {
    val currentUser: FeatureUser?
}
