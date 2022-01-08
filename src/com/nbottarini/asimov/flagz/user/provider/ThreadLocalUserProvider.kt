package com.nbottarini.asimov.flagz.user.provider

import com.nbottarini.asimov.flagz.user.FeatureUser

class ThreadLocalUserProvider: UserProvider {
    private var threadLocalCurrentUser: ThreadLocal<FeatureUser?> = ThreadLocal()

    override val currentUser: FeatureUser? get() = threadLocalCurrentUser.get()

    fun bind(user: FeatureUser) {
        threadLocalCurrentUser.set(user)
    }

    fun release() {
        threadLocalCurrentUser.set(null)
    }
}
