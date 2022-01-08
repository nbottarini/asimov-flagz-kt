package com.nbottarini.asimov.flagz.manager.metadata

import com.nbottarini.asimov.flagz.Feature
import com.nbottarini.asimov.flagz.conditionalStrategies.UsersStrategy
import com.nbottarini.asimov.flagz.conditionalStrategies.UsersStrategy.Companion.PARAM_USERS
import com.nbottarini.asimov.flagz.conditionalStrategies.annotations.Conditional
import com.nbottarini.asimov.flagz.conditionalStrategies.annotations.Param
import com.nbottarini.asimov.flagz.EnabledByDefault
import com.nbottarini.asimov.flagz.manager.metadata.MetadataCacheTest.Features.*
import com.nbottarini.asimov.flagz.manager.metadata.MetadataCacheTest.OtherFeatures.OTHER_CONDITIONAL_FEATURE
import com.nbottarini.asimov.flagz.manager.metadata.MetadataCacheTest.OtherFeatures.OTHER_FEATURE
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class MetadataCacheTest {
    @Test
    fun `has metadata for all registered enums`() {
        val metadataCache = MetadataCache(listOf(Features::class.java, OtherFeatures::class.java))

        assertThat(metadataCache.get(SIMPLE_FEATURE)).isEqualTo(SIMPLE_FEATURE.metadata())
        assertThat(metadataCache.get(ENABLED_BY_DEFAULT_FEATURE)).isEqualTo(ENABLED_BY_DEFAULT_FEATURE.metadata())
        assertThat(metadataCache.get(CONDITIONAL_FEATURE)).isEqualTo(CONDITIONAL_FEATURE.metadata())
        assertThat(metadataCache.get(OTHER_FEATURE)).isEqualTo(OTHER_FEATURE.metadata())
        assertThat(metadataCache.get(OTHER_CONDITIONAL_FEATURE)).isEqualTo(OTHER_CONDITIONAL_FEATURE.metadata())
    }

    @Test
    fun `allFeatures returns all features of all registered enums`() {
        val metadataCache = MetadataCache(listOf(Features::class.java, OtherFeatures::class.java))

        assertThat(metadataCache.allFeatures()).containsExactlyInAnyOrder(
            SIMPLE_FEATURE, ENABLED_BY_DEFAULT_FEATURE, CONDITIONAL_FEATURE, OTHER_FEATURE, OTHER_CONDITIONAL_FEATURE
        )
    }

    enum class Features: Feature {
        SIMPLE_FEATURE,

        @EnabledByDefault
        ENABLED_BY_DEFAULT_FEATURE,

        @Conditional(UsersStrategy.ID, [Param(PARAM_USERS, "alice, bob")])
        CONDITIONAL_FEATURE,
    }

    enum class OtherFeatures: Feature {
        OTHER_FEATURE,

        @Conditional(UsersStrategy.ID, [Param(PARAM_USERS, "charlie")])
        OTHER_CONDITIONAL_FEATURE,
    }
}
