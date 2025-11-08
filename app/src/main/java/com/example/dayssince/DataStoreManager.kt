package com.example.dayssince

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import org.json.JSONObject
import java.time.LocalDate

private val Context.dataStore by preferencesDataStore(name = "days_since_prefs")

class DataStoreManager(private val context: Context) {

    companion object {
        private val KEY_COUNTERS_JSON = stringPreferencesKey("counters_json")
    }

    /** Flujo con la lista de contadores persistidos (o lista vacía si no hay nada). */
    val countersFlow: Flow<List<Counter>> =
        context.dataStore.data.map { prefs ->
            val json = prefs[KEY_COUNTERS_JSON] ?: "[]"
            decodeCounters(json)
        }

    /** Guarda la lista completa (KISS). */
    suspend fun saveCounters(counters: List<Counter>) {
        val json = encodeCounters(counters)
        context.dataStore.edit { prefs ->
            prefs[KEY_COUNTERS_JSON] = json
        }
    }

    // --- Utilidades de codificación KISS (JSON) ---

    private fun encodeCounters(list: List<Counter>): String {
        val arr = JSONArray()
        list.forEach { c ->
            val obj = JSONObject().apply {
                put("id", c.id)
                put("title", c.title)
                // ISO-8601: yyyy-MM-dd
                put("startDate", c.startDate.toString())
            }
            arr.put(obj)
        }
        return arr.toString()
    }

    private fun decodeCounters(json: String): List<Counter> {
        return runCatching {
            val arr = JSONArray(json)
            buildList {
                for (i in 0 until arr.length()) {
                    val obj = arr.getJSONObject(i)
                    add(
                        Counter(
                            id = obj.getLong("id"),
                            title = obj.getString("title"),
                            startDate = LocalDate.parse(obj.getString("startDate"))
                        )
                    )
                }
            }
        }.getOrElse { emptyList() }
    }
}
