package com.nbottarini.asimov.flagz

interface Feature {
    val name: String

    val isEnabled get() = FlagzContext.manager.isEnabled(this)
}
