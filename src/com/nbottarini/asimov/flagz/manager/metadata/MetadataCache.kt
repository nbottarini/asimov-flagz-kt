package com.nbottarini.asimov.flagz.manager.metadata

import com.nbottarini.asimov.flagz.Feature
import java.util.concurrent.ConcurrentHashMap

class MetadataCache(private val featureEnums: List<Class<out Feature>>) {
    private val cache = ConcurrentHashMap<Feature, FeatureMetadata>()

    init {
        initializeCache()
    }

    fun get(feature: Feature): FeatureMetadata {
        return cache[feature] ?: feature.metadata()
    }

    fun allFeatures() = cache.keys().toList()

    private fun initializeCache() {
        featureEnums.forEach { clazz ->
            if (!clazz.isEnum) throw IllegalArgumentException("${clazz.simpleName} must be an enum")
            clazz.enumConstants.forEach { feature ->
                cache[feature] = feature.metadata()
            }
        }
    }
}
