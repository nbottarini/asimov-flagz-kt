package com.nbottarini.asimov.flagz.repositories.environment

interface EnvironmentProvider {
    fun get(name: String): String?
}
