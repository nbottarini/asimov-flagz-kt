package com.nbottarini.asimov.flagz.activations

import com.nbottarini.asimov.flagz.Feature
import com.nbottarini.asimov.flagz.activations.UsersActivationStrategy.Companion.PARAM_USERS
import com.nbottarini.asimov.flagz.activations.UsersActivationStrategyTest.Features.*
import com.nbottarini.asimov.flagz.annotations.Activation
import com.nbottarini.asimov.flagz.annotations.ActivationParam
import com.nbottarini.asimov.flagz.metadata.defaultState
import com.nbottarini.asimov.flagz.user.SimpleFeatureUser
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class UsersActivationStrategyTest {
    @Test
    fun `isEnabled returns false if there is no current user`() {
        val isEnabled = strategy.isEnabled(MY_FEATURE.defaultState(), user = null)
        assertThat(isEnabled).isFalse
    }

    @Test
    fun `isEnabled returns false if current user name is not in users list`() {
        val isEnabled = strategy.isEnabled(MY_FEATURE.defaultState(), SimpleFeatureUser("charlie"))
        assertThat(isEnabled).isFalse
    }

    @Test
    fun `isEnabled returns true if current user name is not in users list`() {
        val isEnabled = strategy.isEnabled(MY_FEATURE.defaultState(), SimpleFeatureUser("bob"))
        assertThat(isEnabled).isTrue
    }

    @Test
    fun `isEnabled returns false if users list is empty`() {
        val isEnabled = strategy.isEnabled(EMPTY_USER_LIST_FEATURE.defaultState(), SimpleFeatureUser("bob"))
        assertThat(isEnabled).isFalse
    }

    @Test
    fun `isEnabled returns false if users list is not set`() {
        val isEnabled = strategy.isEnabled(NO_USER_LIST_FEATURE.defaultState(), SimpleFeatureUser("bob"))
        assertThat(isEnabled).isFalse
    }

    private val strategy = UsersActivationStrategy()

    enum class Features: Feature {
        @Activation(UsersActivationStrategy.ID, [ActivationParam(PARAM_USERS, "alice, bob")])
        MY_FEATURE,

        @Activation(UsersActivationStrategy.ID, [ActivationParam(PARAM_USERS, "")])
        EMPTY_USER_LIST_FEATURE,

        @Activation(UsersActivationStrategy.ID)
        NO_USER_LIST_FEATURE,
    }
}
