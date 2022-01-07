package com.nbottarini.asimov.flagz.activations

import com.nbottarini.asimov.time.Clock
import com.nbottarini.asimov.time.LocalDateTimeParser
import com.nbottarini.asimov.flagz.Feature
import com.nbottarini.asimov.flagz.activations.ReleaseDateActivationStrategy.Companion.PARAM_DATE
import com.nbottarini.asimov.flagz.activations.ReleaseDateActivationStrategyTest.Features.*
import com.nbottarini.asimov.flagz.annotations.Activation
import com.nbottarini.asimov.flagz.annotations.ActivationParam
import com.nbottarini.asimov.flagz.metadata.defaultState
import com.nbottarini.asimov.flagz.user.SimpleFeatureUser
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.format.DateTimeParseException

class ReleaseDateActivationStrategyTest {
    @Test
    fun `isEnabled returns true if release date has passed`() {
        Clock.stoppedAt(LocalDateTimeParser().parseISO8601("2020-09-06T11:00:00Z"))

        val isEnabled = strategy.isEnabled(MY_FEATURE.defaultState(), user)

        assertThat(isEnabled).isTrue
    }

    @Test
    fun `isEnabled returns false if release date has not passed`() {
        Clock.stoppedAt(LocalDateTimeParser().parseISO8601("2020-09-06T09:00:00Z"))

        val isEnabled = strategy.isEnabled(MY_FEATURE.defaultState(), user)

        assertThat(isEnabled).isFalse
    }

    @Test
    fun `isEnabled throws error if date is not in ISO-8601`() {
        assertThrows<DateTimeParseException> {
            strategy.isEnabled(INVALID_DATE_FEATURE.defaultState(), user)
        }
    }

    @Test
    fun `isEnabled returns false if date is empty`() {
        val isEnabled = strategy.isEnabled(EMPTY_DATE_FEATURE.defaultState(), user)
        assertThat(isEnabled).isFalse
    }

    @Test
    fun `isEnabled returns false if date is not set`() {
        val isEnabled = strategy.isEnabled(NO_DATE_PARAM_FEATURE.defaultState(), user)
        assertThat(isEnabled).isFalse
    }

    private val user = SimpleFeatureUser("bob")
    private val strategy = ReleaseDateActivationStrategy()

    enum class Features: Feature {
        @Activation(ReleaseDateActivationStrategy.ID, [ActivationParam(PARAM_DATE, "2020-09-06T10:00:00Z")])
        MY_FEATURE,

        @Activation(UsersActivationStrategy.ID, [ActivationParam(PARAM_DATE, "")])
        EMPTY_DATE_FEATURE,

        @Activation(UsersActivationStrategy.ID, [ActivationParam(PARAM_DATE, "10/6/2020")])
        INVALID_DATE_FEATURE,

        @Activation(UsersActivationStrategy.ID)
        NO_DATE_PARAM_FEATURE,
    }
}
