package com.nbottarini.asimov.flagz.repositories.environment

import com.nbottarini.asimov.environment.Env

class AsimovEnvironmentProvider: EnvironmentProvider {
    override fun get(name: String) = Env[name]
}
