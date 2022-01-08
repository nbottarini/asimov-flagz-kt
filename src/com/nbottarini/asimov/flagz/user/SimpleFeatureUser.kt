package com.nbottarini.asimov.flagz.user

class SimpleFeatureUser(
    override val name: String,
    override val attributes: Map<String, String> = mapOf(),
): FeatureUser {
    override fun equals(other: Any?) = other is SimpleFeatureUser && other.name == name

    override fun hashCode() = name.hashCode()

    override fun toString() = "SimpleFeatureUser($name, $attributes)"
}
