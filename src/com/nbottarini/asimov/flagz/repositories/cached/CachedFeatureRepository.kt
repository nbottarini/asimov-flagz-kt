package com.nbottarini.asimov.flagz.repositories.cached

import com.nbottarini.asimov.flagz.Feature
import com.nbottarini.asimov.flagz.repositories.FeatureRepository
import com.nbottarini.asimov.flagz.repositories.FeatureState
import java.util.concurrent.ConcurrentHashMap

class CachedFeatureRepository(private val repository: FeatureRepository, private val timeToLiveMs: Long = 60_000): FeatureRepository {
    private val cache = ConcurrentHashMap<Feature, CacheEntry>()

    override fun get(feature: Feature): FeatureState? {
        val cached = getFromCache(feature)
        if (cached != null) return cached

        val state = repository.get(feature)
        cache[feature] = CacheEntry(state)

        return state
    }

    private fun getFromCache(feature: Feature): FeatureState? {
        val entry = cache[feature]
        if (entry == null || isExpired(entry)) return null
        return entry.state
    }

    override fun get(features: List<Feature>): List<FeatureState> {
        val cachedFeatureStates = features.mapNotNull { getFromCache(it) }
        val nonCachedFeatures = features.filter { feature -> cachedFeatureStates.none { it.feature == feature } }
        val nonCachedStates = repository.get(nonCachedFeatures)
        nonCachedFeatures.forEach { feature ->
            val state = nonCachedStates.firstOrNull { it.feature == feature }
            cache[feature] = CacheEntry(state)
        }

        return cachedFeatureStates + nonCachedStates
    }

    private fun isExpired(entry: CacheEntry) = entry.timestampMs + timeToLiveMs < System.currentTimeMillis()

    override fun set(state: FeatureState) {
        repository.set(state)
        cache.remove(state.feature)
    }

    private class CacheEntry(val state: FeatureState?) {
        val timestampMs = System.currentTimeMillis()
    }
}
