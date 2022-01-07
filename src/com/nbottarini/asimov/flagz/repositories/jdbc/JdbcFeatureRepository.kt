package com.nbottarini.asimov.flagz.repositories.jdbc

import com.google.gson.GsonBuilder
import com.nbottarini.asimov.flagz.Feature
import com.nbottarini.asimov.flagz.repositories.FeatureRepository
import com.nbottarini.asimov.flagz.repositories.FeatureState
import java.sql.Connection
import java.sql.SQLException
import javax.sql.DataSource

class JdbcFeatureRepository(
    private val dataSource: DataSource,
    private val tableName: String = "feature_flags",
    generateSchema: Boolean = true,
): FeatureRepository {
    private val serializer = GsonBuilder().create()

    init {
        if (generateSchema) generateSchema()
    }

    private fun generateSchema() {
        try {
            dataSource.connection.use { connection ->
                if (tableExists(connection)) return
                createTable(connection)
            }
        } catch (e: SQLException) {
            throw IllegalStateException("Failed to generate schema", e)
        }
    }

    private fun tableExists(connection: Connection): Boolean {
        connection.createStatement().use { statement ->
            statement.executeQuery("SELECT COUNT(*) FROM information_schema.tables WHERE table_name = '$tableName'").use { result ->
                result.next()
                return result.getInt(1) == 1
            }
        }
    }

    private fun createTable(connection: Connection) {
        connection.createStatement().use { statement ->
            statement.execute("""
                CREATE TABLE $tableName (
                    name                VARCHAR(100) PRIMARY KEY,
                    is_enabled          INTEGER NOT NULL,
                    strategy_id         VARCHAR(200),
                    strategy_params     VARCHAR(2000)
                )
            """.trimIndent())
        }
    }

    override fun get(feature: Feature): FeatureState? {
        try {
            dataSource.connection.use { connection ->
                connection.prepareStatement(
                    "SELECT is_enabled, strategy_id, strategy_params FROM $tableName WHERE name = ?"
                ).use { statement ->
                    statement.setString(1, feature.name.lowercase())
                    statement.executeQuery().use { result ->
                        if (!result.next()) return null

                        val isEnabled = result.getInt("is_enabled") == 1
                        val strategyId = result.getString("strategy_id")
                        val strategyParams = serializer.fromJson<Map<String, String>>(result.getString("strategy_params"), Map::class.java)
                        return FeatureState(feature, isEnabled, strategyId, strategyParams)
                    }
                }
            }
        } catch (e: SQLException) {
            throw IllegalStateException("Failed to fetch feature state from database", e)
        }
    }

    override fun get(features: List<Feature>): List<FeatureState> {
        val states = mutableListOf<FeatureState>()
        try {
            dataSource.connection.use { connection ->
                connection.prepareStatement(
                    "SELECT name, is_enabled, strategy_id, strategy_params FROM $tableName"
                ).use { statement ->
                    statement.executeQuery().use { result ->
                        while(result.next()) {
                            val name = result.getString("name")
                            val feature = features.firstOrNull { it.name.lowercase() == name.lowercase() } ?: continue
                            val isEnabled = result.getInt("is_enabled") == 1
                            val strategyId = result.getString("strategy_id")
                            val strategyParams = serializer.fromJson<Map<String, String>>(result.getString("strategy_params"), Map::class.java)
                            states.add(FeatureState(feature, isEnabled, strategyId, strategyParams))
                        }
                    }
                }
            }
        } catch (e: SQLException) {
            throw IllegalStateException("Failed to fetch feature states from database", e)
        }
        return states
    }

    override fun set(state: FeatureState) {
        try {
            dataSource.connection.use { connection ->
                tryUpdateFeature(connection, state) {
                    insertFeature(connection, state)
                }
            }
        } catch (e: SQLException) {
            throw IllegalStateException("Failed to set feature state in database", e)
        }
    }

    private fun tryUpdateFeature(connection: Connection, state: FeatureState, onFailure: () -> Unit) {
        connection.prepareStatement(
            "UPDATE $tableName SET is_enabled = ?, strategy_id = ?, strategy_params = ? WHERE name = ?"
        ).use { statement ->
            statement.setInt(1, if (state.isEnabled) 1 else 0)
            statement.setString(2, state.strategyId)
            statement.setString(3, serializer.toJson(state.strategyParams))
            statement.setString(4, state.feature.name.lowercase())
            val updatedRows = statement.executeUpdate()
            if (updatedRows == 0) onFailure()
        }
    }

    private fun insertFeature(connection: Connection, state: FeatureState) {
        connection.prepareStatement(
            "INSERT INTO $tableName (name, is_enabled, strategy_id, strategy_params) VALUES (?, ?, ?, ?)"
        ).use { statement ->
            statement.setString(1, state.feature.name.lowercase())
            statement.setInt(2, if (state.isEnabled) 1 else 0)
            statement.setString(3, state.strategyId)
            statement.setString(4, serializer.toJson(state.strategyParams))
            statement.executeUpdate()
        }
    }
}
