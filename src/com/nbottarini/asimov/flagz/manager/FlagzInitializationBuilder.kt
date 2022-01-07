package com.nbottarini.asimov.flagz.manager

import com.nbottarini.asimov.flagz.Feature
import com.nbottarini.asimov.flagz.FlagzContext
import com.nbottarini.asimov.flagz.activations.ActivationStrategy
import com.nbottarini.asimov.flagz.repositories.FeatureRepository
import com.nbottarini.asimov.flagz.repositories.composite.CompositeFeatureRepository
import com.nbottarini.asimov.flagz.repositories.inMemory.InMemoryFeatureRepository
import com.nbottarini.asimov.flagz.user.ThreadLocalUserProvider
import com.nbottarini.asimov.flagz.user.UserProvider

class FlagzInitializationBuilder {
    private var featureEnums = mutableListOf<Class<out Feature>>()
    private var repository: FeatureRepository = InMemoryFeatureRepository()
    private var userProvider: UserProvider = ThreadLocalUserProvider()
    private var activationStrategies = mutableListOf<ActivationStrategy>()

    fun featureEnum(value: Class<out Feature>) = apply { featureEnums.add(value) }

    inline fun <reified T: Feature> featureEnum() = featureEnum(T::class.java)

    fun repository(value: FeatureRepository) = apply { repository = value }

    fun repositories(vararg values: FeatureRepository) = apply { repository = CompositeFeatureRepository(values.toList()) }

    fun userProvider(value: UserProvider) = apply { userProvider = value }

    fun activationStrategy(value: ActivationStrategy) = apply { activationStrategies.add(value) }

    fun build(): FlagzManager {
        val manager = DefaultFlagzManager(featureEnums, repository, userProvider)
        activationStrategies.forEach { manager.addActivationStrategy(it) }
        FlagzContext.init(manager)
        return manager
    }
}

fun initFlagz(addDetails: FlagzInitializationBuilder.() -> Unit) = FlagzInitializationBuilder().also(addDetails).build()
