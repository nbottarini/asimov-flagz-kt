package com.nbottarini.asimov.flagz.conditionalStrategies

import com.nbottarini.asimov.flagz.Feature
import com.nbottarini.asimov.flagz.conditionalStrategies.UsersStrategy.Companion.PARAM_USERS
import com.nbottarini.asimov.flagz.conditionalStrategies.UsersStrategyTest.Features.*
import com.nbottarini.asimov.flagz.manager.metadata.defaultState
import com.nbottarini.asimov.flagz.user.SimpleFeatureUser
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class UsersStrategyTest {
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

    private val strategy = UsersStrategy()

    enum class Features: Feature {
        @Conditional(UsersStrategy.ID, [Param(PARAM_USERS, "alice, bob")])
        MY_FEATURE,

        @Conditional(UsersStrategy.ID, [Param(PARAM_USERS, "")])
        EMPTY_USER_LIST_FEATURE,

        @Conditional(UsersStrategy.ID)
        NO_USER_LIST_FEATURE,
    }
}
