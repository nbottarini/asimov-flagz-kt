package com.nbottarini.asimov.flagz.metadata

import com.nbottarini.asimov.flagz.Feature
import com.nbottarini.asimov.flagz.activations.UsersActivationStrategy
import com.nbottarini.asimov.flagz.activations.UsersActivationStrategy.Companion.PARAM_USERS
import com.nbottarini.asimov.flagz.annotations.Activation
import com.nbottarini.asimov.flagz.annotations.ActivationParam
import com.nbottarini.asimov.flagz.annotations.EnabledByDefault
import com.nbottarini.asimov.flagz.metadata.MetadataCacheTest.Features.*
import com.nbottarini.asimov.flagz.metadata.MetadataCacheTest.OtherFeatures.OTHER_ACTIVATION_FEATURE
import com.nbottarini.asimov.flagz.metadata.MetadataCacheTest.OtherFeatures.OTHER_FEATURE
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class MetadataCacheTest {
    @Test
    fun `has metadata for all registered enums`() {
        val metadataCache = MetadataCache(listOf(Features::class.java, OtherFeatures::class.java))

        assertThat(metadataCache.get(SIMPLE_FEATURE)).isEqualTo(SIMPLE_FEATURE.metadata())
        assertThat(metadataCache.get(ENABLED_BY_DEFAULT_FEATURE)).isEqualTo(ENABLED_BY_DEFAULT_FEATURE.metadata())
        assertThat(metadataCache.get(ACTIVATION_FEATURE)).isEqualTo(ACTIVATION_FEATURE.metadata())
        assertThat(metadataCache.get(OTHER_FEATURE)).isEqualTo(OTHER_FEATURE.metadata())
        assertThat(metadataCache.get(OTHER_ACTIVATION_FEATURE)).isEqualTo(OTHER_ACTIVATION_FEATURE.metadata())
    }

    @Test
    fun `allFeatures returns all features of all registered enums`() {
        val metadataCache = MetadataCache(listOf(Features::class.java, OtherFeatures::class.java))

        assertThat(metadataCache.allFeatures()).containsExactlyInAnyOrder(
            SIMPLE_FEATURE, ENABLED_BY_DEFAULT_FEATURE, ACTIVATION_FEATURE, OTHER_FEATURE, OTHER_ACTIVATION_FEATURE
        )
    }

    enum class Features: Feature {
        SIMPLE_FEATURE,

        @EnabledByDefault
        ENABLED_BY_DEFAULT_FEATURE,

        @Activation(UsersActivationStrategy.ID, [ActivationParam(PARAM_USERS, "alice, bob")])
        ACTIVATION_FEATURE,
    }

    enum class OtherFeatures: Feature {
        OTHER_FEATURE,

        @Activation(UsersActivationStrategy.ID, [ActivationParam(PARAM_USERS, "charlie")])
        OTHER_ACTIVATION_FEATURE,
    }
}
