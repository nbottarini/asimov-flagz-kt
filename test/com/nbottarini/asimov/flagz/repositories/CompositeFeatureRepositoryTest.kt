package com.nbottarini.asimov.flagz.repositories

import com.nbottarini.asimov.flagz.Feature
import com.nbottarini.asimov.flagz.repositories.CompositeFeatureRepositoryTest.Features.*
import com.nbottarini.asimov.flagz.repositories.composite.CompositeFeatureRepository
import com.nbottarini.asimov.flagz.repositories.composite.CompositeFeatureRepository.SetStrategies.*
import com.nbottarini.asimov.flagz.repositories.inMemory.InMemoryFeatureRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class CompositeFeatureRepositoryTest {
    @Test
    fun `get returns from first repository if multiple repositories have the state`() {
        repository1.set(FeatureState(FEATURE_1, true))
        repository2.set(FeatureState(FEATURE_1, false))

        val state = composite.get(FEATURE_1)

        assertThat(state?.isEnabled).isTrue
    }

    @Test
    fun `get returns from first repository that has the state`() {
        repository2.set(FeatureState(FEATURE_1, false))

        val state = composite.get(FEATURE_1)

        assertThat(state?.isEnabled).isFalse
    }

    @Test
    fun `get returns null if no repository has the state`() {
        val state = composite.get(FEATURE_1)

        assertThat(state).isNull()
    }


    @Test
    fun `get multiple features returns each state from first matching repository`() {
        repository1.set(FeatureState(FEATURE_1, true))
        repository2.set(FeatureState(FEATURE_1, false))
        repository2.set(FeatureState(FEATURE_2, true))
        repository2.set(FeatureState(FEATURE_3, true))

        val states = composite.get(listOf(FEATURE_1, FEATURE_2, FEATURE_3, FEATURE_4))

        assertThat(states.map { it.feature }).containsExactlyInAnyOrder(FEATURE_1, FEATURE_2, FEATURE_3)
        assertThat(states.single { it.feature == FEATURE_1 }.isEnabled).isTrue
        assertThat(states.single { it.feature == FEATURE_2 }.isEnabled).isTrue
        assertThat(states.single { it.feature == FEATURE_3 }.isEnabled).isTrue
    }

    @Test
    fun `set only in first repository if setStrategy is FIRST`() {
        val composite = CompositeFeatureRepository(repository1, repository2, setStrategy = FIRST)

        composite.set(FeatureState(FEATURE_1, true))

        assertThat(repository1.get(FEATURE_1)?.isEnabled).isTrue
        assertThat(repository2.get(FEATURE_1)).isNull()
    }

    @Test
    fun `set only in last repository if setStrategy is LAST`() {
        val composite = CompositeFeatureRepository(repository1, repository2, setStrategy = LAST)

        composite.set(FeatureState(FEATURE_1, true))

        assertThat(repository1.get(FEATURE_1)).isNull()
        assertThat(repository2.get(FEATURE_1)?.isEnabled).isTrue
    }

    @Test
    fun `set in all repositories if setStrategy is ALL`() {
        val composite = CompositeFeatureRepository(repository1, repository2, setStrategy = ALL)

        composite.set(FeatureState(FEATURE_1, true))

        assertThat(repository1.get(FEATURE_1)?.isEnabled).isTrue
        assertThat(repository2.get(FEATURE_1)?.isEnabled).isTrue
    }

    private val repository1 = InMemoryFeatureRepository()
    private val repository2 = InMemoryFeatureRepository()
    private val composite = CompositeFeatureRepository(repository1, repository2)

    enum class Features: Feature {
        FEATURE_1,
        FEATURE_2,
        FEATURE_3,
        FEATURE_4,
    }
}
