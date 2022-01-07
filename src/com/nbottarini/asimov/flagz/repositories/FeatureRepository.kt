package com.nbottarini.asimov.flagz.repositories

import com.nbottarini.asimov.flagz.Feature
import com.nbottarini.asimov.flagz.repositories.cached.CachedFeatureRepository

interface FeatureRepository {
    fun get(feature: Feature): FeatureState?
    fun get(features: List<Feature>): List<FeatureState>
    fun set(state: FeatureState)
    fun cached() = CachedFeatureRepository(this)
    fun cached(timeToLiveMs: Long) = CachedFeatureRepository(this, timeToLiveMs)
}
