package com.nbottarini.asimov.flagz.repositories.environment

import com.nbottarini.asimov.environment.Environment

class AsimovEnvironmentProvider: EnvironmentProvider {
    override fun get(name: String) = Environment[name]
}
