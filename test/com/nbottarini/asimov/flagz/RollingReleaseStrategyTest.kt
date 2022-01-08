package com.nbottarini.asimov.flagz

import com.nbottarini.asimov.flagz.RollingReleaseStrategyTest.Features.MY_ROLLING_FEATURE
import com.nbottarini.asimov.flagz.conditionalStrategies.Conditional
import com.nbottarini.asimov.flagz.conditionalStrategies.Param
import com.nbottarini.asimov.flagz.conditionalStrategies.RollingReleaseStrategy
import com.nbottarini.asimov.flagz.conditionalStrategies.RollingReleaseStrategy.Companion.PARAM_PERCENTAGE
import com.nbottarini.asimov.flagz.conditionalStrategies.StringHashCodeGenerator
import com.nbottarini.asimov.flagz.manager.metadata.defaultState
import com.nbottarini.asimov.flagz.repositories.FeatureState
import com.nbottarini.asimov.flagz.user.SimpleFeatureUser
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class RollingReleaseStrategyTest {
    @Test
    fun `isEnabled returns false if user is null`() {
        val isEnabled = strategy.isEnabled(MY_ROLLING_FEATURE.defaultState(), null)
        assertThat(isEnabled).isFalse
    }

    @Test
    fun `isEnabled returns false if user name is blank`() {
        val isEnabled = strategy.isEnabled(MY_ROLLING_FEATURE.defaultState(), SimpleFeatureUser(" "))
        assertThat(isEnabled).isFalse
    }

    @ParameterizedTest
    @ValueSource(strings = ["", "as", "-1", "0"])
    fun `isEnabled returns false if percentage is invalid`(invalidValue: String) {
        val isEnabled = strategy.isEnabled(featureWithPercentage(invalidValue), aUser)
        assertThat(isEnabled).isFalse
    }

    @ParameterizedTest
    @ValueSource(ints = [0, 1, 50, 100, 150])
    fun `isEnabled returns true for all users if percent is 100`(userHashCode: Int) {
        val isEnabled = strategy.isEnabled(featureWithPercentage("100"), userWithHashCode(userHashCode))
        assertThat(isEnabled).isTrue
    }

    @ParameterizedTest
    @ValueSource(ints = [0, 1, 10, 30, 44])
    fun `isEnabled returns true for all users with hashcode lower than percent`(userHashCode: Int) {
        val isEnabled = strategy.isEnabled(featureWithPercentage("45"), userWithHashCode(userHashCode))
        assertThat(isEnabled).isTrue
    }

    @ParameterizedTest
    @ValueSource(ints = [100, 101, 110, 130, 144])
    fun `isEnabled returns true for all users with hashcode mod 100 lower than percent`(userHashCode: Int) {
        val isEnabled = strategy.isEnabled(featureWithPercentage("45"), userWithHashCode(userHashCode))
        assertThat(isEnabled).isTrue
    }

    @ParameterizedTest
    @ValueSource(ints = [45, 46, 50, 75, 90, 99])
    fun `isEnabled returns false for all users with hashcode greater than percent`(userHashCode: Int) {
        val isEnabled = strategy.isEnabled(featureWithPercentage("45"), userWithHashCode(userHashCode))
        assertThat(isEnabled).isFalse
    }

    @ParameterizedTest
    @ValueSource(ints = [145, 146, 150, 175, 190, 199])
    fun `isEnabled returns false for all users with hashcode mod 100 greater than percent`(userHashCode: Int) {
        val isEnabled = strategy.isEnabled(featureWithPercentage("45"), userWithHashCode(userHashCode))
        assertThat(isEnabled).isFalse
    }

    private fun featureWithPercentage(percentage: String): FeatureState {
        return MY_ROLLING_FEATURE.defaultState().withParam(PARAM_PERCENTAGE, percentage)
    }

    private fun userWithHashCode(value: Int) = SimpleFeatureUser(value.toString())

    private val hashCodeGenerator = FakeHashCodeGenerator()
    private val aUser = SimpleFeatureUser("alice")
    private val strategy = RollingReleaseStrategy(hashCodeGenerator)

    enum class Features: Feature {
        @Conditional(RollingReleaseStrategy.ID, [Param(PARAM_PERCENTAGE, "10")])
        MY_ROLLING_FEATURE
    }
}

class FakeHashCodeGenerator: StringHashCodeGenerator {
    override fun calculateFor(value: String) = value.split(":").first().toIntOrNull() ?: 0
}
