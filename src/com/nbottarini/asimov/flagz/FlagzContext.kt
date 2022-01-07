package com.nbottarini.asimov.flagz

import com.nbottarini.asimov.flagz.manager.FlagzManager
import com.nbottarini.asimov.flagz.manager.SimpleFlagzManager

object FlagzContext {
    var manager: FlagzManager = SimpleFlagzManager()
        private set

    fun init(manager: FlagzManager) {
        this.manager = manager
    }
}
