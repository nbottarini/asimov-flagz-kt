package com.nbottarini.asimov.flagz.repositories

import com.nbottarini.asimov.flagz.Feature
import com.nbottarini.asimov.flagz.repositories.jdbc.JdbcFeatureRepository
import org.assertj.core.api.Assertions.assertThat
import org.h2.jdbcx.JdbcDataSource
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import javax.sql.DataSource

class JdbcFeatureRepositoryTest {
    @Test
    fun `generates schema on creation`() {
        JdbcFeatureRepository(dataSource, tableName = "feature_flags", generateSchema = true)

        assertDoesNotThrow {
            executeSql("SELECT * FROM feature_flags")
        }
    }

    @Test
    fun `don't generate schema if exists`() {
        JdbcFeatureRepository(dataSource, tableName = "feature_flags", generateSchema = true)

        assertDoesNotThrow {
            JdbcFeatureRepository(dataSource, tableName = "feature_flags", generateSchema = true)
        }
    }

    @Test
    fun `set persists enabled true`() {
        val repository = JdbcFeatureRepository(dataSource)

        repository.set(FeatureState(Features.MY_FEATURE, isEnabled = true))

        val state = repository.get(Features.MY_FEATURE)
        assertThat(state?.isEnabled).isTrue
    }

    @Test
    fun `set persists enabled false`() {
        val repository = JdbcFeatureRepository(dataSource)

        repository.set(FeatureState(Features.MY_FEATURE, isEnabled = false))

        val state = repository.get(Features.MY_FEATURE)
        assertThat(state?.isEnabled).isFalse
    }

    @Test
    fun `set persists strategy id`() {
        val repository = JdbcFeatureRepository(dataSource)

        repository.set(FeatureState(Features.MY_FEATURE, true, strategyId = "my-strategy"))

        val state = repository.get(Features.MY_FEATURE)
        assertThat(state?.strategyId).isEqualTo("my-strategy")
    }

    @Test
    fun `set persists strategy params`() {
        val repository = JdbcFeatureRepository(dataSource)
        val params = mapOf("param1" to "value1", "param2" to "value2")

        repository.set(FeatureState(Features.MY_FEATURE, true, strategyParams = params))

        val state = repository.get(Features.MY_FEATURE)
        assertThat(state?.strategyParams).isEqualTo(params)
    }

    @Test
    fun `get multiple features`() {
        val repository = JdbcFeatureRepository(dataSource)
        repository.set(FeatureState(Features.MY_FEATURE, true))
        repository.set(FeatureState(Features.MY_OTHER_FEATURE, true))

        val states = repository.get(listOf(Features.MY_FEATURE, Features.MY_OTHER_FEATURE, Features.YET_ANOTHER_FEATURE))

        assertThat(states.map { it.feature }).containsExactlyInAnyOrder(Features.MY_FEATURE, Features.MY_OTHER_FEATURE)
    }

    @BeforeEach
    fun beforeEach() {
        dataSource.connection.use { connection ->
            connection.createStatement().execute("DROP ALL OBJECTS")
        }
    }

    private fun executeSql(sql: String) {
        dataSource.connection.use { connection ->
            connection.createStatement().executeQuery(sql)
        }
    }

    private fun createDataSource(): DataSource {
        val ds = JdbcDataSource()
        ds.setURL("jdbc:h2:mem:myDb;DB_CLOSE_DELAY=-1;IGNORECASE=TRUE;CASE_INSENSITIVE_IDENTIFIERS=TRUE")
        return ds
    }

    private val dataSource = createDataSource()

    enum class Features: Feature {
        MY_FEATURE,
        MY_OTHER_FEATURE,
        YET_ANOTHER_FEATURE,
    }
}
