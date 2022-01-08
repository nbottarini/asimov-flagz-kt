package com.nbottarini.asimov.flagz.conditionalStrategies

import com.nbottarini.asimov.flagz.Feature
import com.nbottarini.asimov.flagz.conditionalStrategies.RollingReleaseStrategyTest.Features.MY_ROLLING_FEATURE
import com.nbottarini.asimov.flagz.conditionalStrategies.UserAttributeStrategy.Companion.PARAM_NAME
import com.nbottarini.asimov.flagz.conditionalStrategies.UserAttributeStrategy.Companion.PARAM_VALUE
import com.nbottarini.asimov.flagz.conditionalStrategies.UserAttributeStrategyTest.Features.FEATURE_WITHOUT_ATTR_NAME
import com.nbottarini.asimov.flagz.conditionalStrategies.UserAttributeStrategyTest.Features.MY_FEATURE
import com.nbottarini.asimov.flagz.conditionalStrategies.annotations.Conditional
import com.nbottarini.asimov.flagz.conditionalStrategies.annotations.Param
import com.nbottarini.asimov.flagz.conditionalStrategies.rollingRelease.RollingReleaseStrategy.Companion.PARAM_PERCENTAGE
import com.nbottarini.asimov.flagz.manager.metadata.defaultState
import com.nbottarini.asimov.flagz.repositories.FeatureState
import com.nbottarini.asimov.flagz.user.SimpleFeatureUser
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class UserAttributeStrategyTest {
    @Test
    fun `isEnabled returns false if user is null`() {
        val isEnabled = strategy.isEnabled(MY_FEATURE.defaultState(), null)
        assertThat(isEnabled).isFalse
    }

    @Test
    fun `isEnabled returns false if feature don't have attribute name`() {
        val isEnabled = strategy.isEnabled(FEATURE_WITHOUT_ATTR_NAME.defaultState(), aUser)
        assertThat(isEnabled).isFalse
    }

    @Test
    fun `isEnabled returns false if attribute name is blank`() {
        val isEnabled = strategy.isEnabled(featureWithAttr(name = "  ", value = "3"), aUser)
        assertThat(isEnabled).isFalse
    }

    @Test
    fun `isEnabled returns false if user doesn't have attribute`() {
        val isEnabled = strategy.isEnabled(featureWithAttr("age", "18"), aUser)
        assertThat(isEnabled).isFalse
    }

    @Test
    fun `isEnabled returns true if user has attribute with same value`() {
        val isEnabled = strategy.isEnabled(featureWithAttr("age", "18"), userWithAttr("age", "18"))
        assertThat(isEnabled).isTrue
    }

    @Test
    fun `isEnabled returns false if user has attribute with different value`() {
        val isEnabled = strategy.isEnabled(featureWithAttr("age", "18"), userWithAttr("age", "20"))
        assertThat(isEnabled).isFalse
    }

    @Test
    fun `isEnabled returns false if user has attribute with different case`() {
        val isEnabled = strategy.isEnabled(featureWithAttr("country", "Argentina"), userWithAttr("country", "ARGENTINA"))
        assertThat(isEnabled).isFalse
    }

    @Test
    fun `isEnabled returns true if user doesn't have attribute but feature attribute value is empty`() {
        val isEnabled = strategy.isEnabled(featureWithAttr("age", ""), aUser)
        assertThat(isEnabled).isTrue
    }

    private fun featureWithAttr(name: String, value: String): FeatureState {
        return MY_FEATURE.defaultState().withParam(PARAM_NAME, name).withParam(PARAM_VALUE, value)
    }

    private fun userWithAttr(name: String, value: String) = SimpleFeatureUser("alice", mapOf(name to value))

    private val aUser = SimpleFeatureUser("alice")
    private val strategy = UserAttributeStrategy()

    enum class Features: Feature {
        @Conditional(UserAttributeStrategy.ID, [Param(PARAM_NAME, "some-attr"), Param(PARAM_VALUE, "some-value")])
        MY_FEATURE,

        @Conditional(UserAttributeStrategy.ID)
        FEATURE_WITHOUT_ATTR_NAME,
    }
}
