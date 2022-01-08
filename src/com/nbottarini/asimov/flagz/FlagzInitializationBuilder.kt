package com.nbottarini.asimov.flagz

import com.nbottarini.asimov.flagz.conditionalStrategies.ConditionalStrategy
import com.nbottarini.asimov.flagz.manager.DefaultFlagzManager
import com.nbottarini.asimov.flagz.manager.FlagzManager
import com.nbottarini.asimov.flagz.repositories.FeatureRepository
import com.nbottarini.asimov.flagz.repositories.composite.CompositeFeatureRepository
import com.nbottarini.asimov.flagz.repositories.inMemory.InMemoryFeatureRepository
import com.nbottarini.asimov.flagz.user.provider.ThreadLocalUserProvider
import com.nbottarini.asimov.flagz.user.provider.UserProvider

class FlagzInitializationBuilder {
    private var featureEnums = mutableListOf<Class<out Feature>>()
    private var repository: FeatureRepository = InMemoryFeatureRepository()
    private var userProvider: UserProvider = ThreadLocalUserProvider()
    private var conditionalStrategies = mutableListOf<ConditionalStrategy>()

    fun featureEnum(value: Class<out Feature>) = apply { featureEnums.add(value) }

    inline fun <reified T: Feature> featureEnum() = featureEnum(T::class.java)

    fun repository(value: FeatureRepository) = apply { repository = value }

    fun repositories(vararg values: FeatureRepository) = apply { repository = CompositeFeatureRepository(values.toList()) }

    fun userProvider(value: UserProvider) = apply { userProvider = value }

    fun conditionalStrategy(value: ConditionalStrategy) = apply { conditionalStrategies.add(value) }

    fun build(): FlagzManager {
        val manager = DefaultFlagzManager(featureEnums, repository, userProvider)
        conditionalStrategies.forEach { manager.addConditionalStrategy(it) }
        FlagzContext.init(manager)
        return manager
    }
}

fun initFlagz(addDetails: FlagzInitializationBuilder.() -> Unit) = FlagzInitializationBuilder().also(addDetails).build()
