package com.nbottarini.asimov.flagz.user

class SimpleFeatureUser(
    override val name: String,
    override val attributes: Map<String, String> = mapOf(),
): FeatureUser {
    fun getAttribute(name: String) = attributes[name]
}
