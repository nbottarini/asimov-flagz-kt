package com.nbottarini.asimov.flagz.manager

import com.nbottarini.asimov.flagz.Feature
import com.nbottarini.asimov.flagz.conditionalStrategies.UsersStrategy
import com.nbottarini.asimov.flagz.conditionalStrategies.annotations.Conditional
import com.nbottarini.asimov.flagz.conditionalStrategies.annotations.Param
import com.nbottarini.asimov.flagz.EnabledByDefault
import com.nbottarini.asimov.flagz.repositories.FeatureState
import com.nbottarini.asimov.flagz.repositories.inMemory.InMemoryFeatureRepository
import com.nbottarini.asimov.flagz.user.SimpleFeatureUser
import com.nbottarini.asimov.flagz.user.provider.ThreadLocalUserProvider
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class DefaultFlagzManagerTest {
    @Test
    fun `isEnabled returns true if a feature is enabled`() {
        manager.enable(Features.FEATURE_1)

        assertThat(manager.isEnabled(Features.FEATURE_1)).isTrue
    }

    @Test
    fun `isEnabled returns false if a feature is disabled`() {
        manager.disable(Features.FEATURE_1)

        assertThat(manager.isEnabled(Features.FEATURE_1)).isFalse
    }

    @Test
    fun `isEnabled returns false if a feature is not set`() {
        assertThat(manager.isEnabled(Features.FEATURE_1)).isFalse
    }

    @Test
    fun `isEnabled returns true if a feature is enabled in the repository`() {
        repository.set(FeatureState(Features.FEATURE_1, isEnabled = true))

        assertThat(manager.isEnabled(Features.FEATURE_1)).isTrue
    }

    @Test
    fun `isEnabled returns false if a feature is disabled in the repository`() {
        repository.set(FeatureState(Features.FEATURE_1, isEnabled = false))

        assertThat(manager.isEnabled(Features.FEATURE_1)).isFalse
    }

    @Test
    fun `isEnabled returns true if a feature is not set and annotated with EnabledByDefault`() {
        assertThat(manager.isEnabled(Features.ENABLED_DEFAULT_FEATURE)).isTrue
    }

    @Test
    fun `isEnabled returns false if a feature is disabled and annotated with EnabledByDefault`() {
        manager.disable(Features.ENABLED_DEFAULT_FEATURE)

        assertThat(manager.isEnabled(Features.ENABLED_DEFAULT_FEATURE)).isFalse
    }

    @Test
    fun `isEnabled returns true if a feature is enabled and meets conditional for a user`() {
        manager.enable(Features.USER_FEATURE)
        userProvider.bind(SimpleFeatureUser("alice"))

        assertThat(manager.isEnabled(Features.USER_FEATURE)).isTrue
    }

    @Test
    fun `isEnabled returns false if a feature meets conditional for a user but is disabled`() {
        manager.disable(Features.USER_FEATURE)
        userProvider.bind(SimpleFeatureUser("alice"))

        assertThat(manager.isEnabled(Features.USER_FEATURE)).isFalse
    }

    @Test
    fun `isEnabled returns false if a feature is enabled but not meets conditional for a user`() {
        manager.enable(Features.USER_FEATURE)
        userProvider.bind(SimpleFeatureUser("bob"))

        assertThat(manager.isEnabled(Features.USER_FEATURE)).isFalse
    }

    @Test
    fun `enable sets feature in the repository`() {
        manager.enable(Features.FEATURE_1)

        assertThat(repository.get(Features.FEATURE_1)!!.isEnabled).isTrue
    }

    @Test
    fun `disable sets feature in the repository`() {
        manager.disable(Features.FEATURE_1)

        assertThat(repository.get(Features.FEATURE_1)!!.isEnabled).isFalse
    }

    @Test
    fun `allFeatures returns all features`() {
        val features = manager.allFeatures()

        assertThat(features).containsExactlyInAnyOrder(
            Features.FEATURE_1, Features.FEATURE_2, Features.ENABLED_DEFAULT_FEATURE, Features.USER_FEATURE,
        )
    }

    @Test
    fun `allEnabled returns all enabled features`() {
        manager.enable(Features.FEATURE_1)
        manager.disable(Features.FEATURE_2)

        val features = manager.allEnabled()

        assertThat(features).containsExactlyInAnyOrder(Features.FEATURE_1, Features.ENABLED_DEFAULT_FEATURE)
    }

    private val repository = InMemoryFeatureRepository()
    private val userProvider = ThreadLocalUserProvider()
    private val manager = DefaultFlagzManager(Features::class.java, repository, userProvider)

    enum class Features: Feature {
        FEATURE_1,
        FEATURE_2,

        @EnabledByDefault
        ENABLED_DEFAULT_FEATURE,

        @Conditional(UsersStrategy.ID, [
            Param(UsersStrategy.PARAM_USERS, "alice")
        ])
        USER_FEATURE,
    }
}
