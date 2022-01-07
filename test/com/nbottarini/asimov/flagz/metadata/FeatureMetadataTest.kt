package com.nbottarini.asimov.flagz.metadata

import com.nbottarini.asimov.flagz.Feature
import com.nbottarini.asimov.flagz.activations.UsersActivationStrategy
import com.nbottarini.asimov.flagz.activations.UsersActivationStrategy.Companion.PARAM_USERS
import com.nbottarini.asimov.flagz.annotations.Activation
import com.nbottarini.asimov.flagz.annotations.ActivationParam
import com.nbottarini.asimov.flagz.annotations.EnabledByDefault
import com.nbottarini.asimov.flagz.metadata.FeatureMetadataTest.Features.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class FeatureMetadataTest {
    @Test
    fun `feature is enabled by default if has the EnabledByDefault annotation`() {
        assertThat(ENABLED_BY_DEFAULT_FEATURE.metadata().isEnabledByDefault).isTrue
    }

    @Test
    fun `feature is not enabled by default if has the EnabledByDefault annotation`() {
        assertThat(SIMPLE_FEATURE.metadata().isEnabledByDefault).isFalse
    }

    @Test
    fun `feature metadata has activation strategy id if has an Activation annotation`() {
        assertThat(ACTIVATION_FEATURE.metadata().strategyId).isEqualTo(UsersActivationStrategy.ID)
    }

    @Test
    fun `feature metadata has activation strategy parameters if has an Activation annotation`() {
        assertThat(ACTIVATION_FEATURE.metadata().strategyParams).isEqualTo(mapOf(PARAM_USERS to "alice, bob"))
    }

    @Test
    fun `defaultState is based on metadata info`() {
        val metadata = FeatureMetadata(SIMPLE_FEATURE, isEnabledByDefault = true, "some-id", mapOf("param" to "value"))

        val state = metadata.defaultState()
        assertThat(state.feature).isEqualTo(metadata.feature)
        assertThat(state.isEnabled).isEqualTo(metadata.isEnabledByDefault)
        assertThat(state.strategyId).isEqualTo(metadata.strategyId)
        assertThat(state.strategyParams).isEqualTo(metadata.strategyParams)
    }

    enum class Features: Feature {
        SIMPLE_FEATURE,

        @EnabledByDefault
        ENABLED_BY_DEFAULT_FEATURE,

        @Activation(UsersActivationStrategy.ID, [ActivationParam(PARAM_USERS, "alice, bob")])
        ACTIVATION_FEATURE,
    }
}
