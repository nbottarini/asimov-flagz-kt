package com.nbottarini.asimov.flagz.user

interface FeatureUser {
    val name: String
    val attributes: Map<String, String>

    fun getAttribute(name: String) = attributes[name]
}
