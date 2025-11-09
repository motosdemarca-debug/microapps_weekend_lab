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

private val Context.dataStore by preferencesDataStore(name = "days_since_store")

class DataStoreManager(private val context: Context) {

    companion object {
        private val COUNTERS_KEY = stringPreferencesKey("counters_json")
    }

    // Save the list as a JSON string
    suspend fun saveCounters(counters: List<Counter>) {
        val jsonArray = JSONArray()
        for (c in counters) {
            val obj = JSONObject()
            obj.put("id", c.id)
            obj.put("title", c.title)
            obj.put("startDate", c.startDate.toString())
            jsonArray.put(obj)
        }
        context.dataStore.edit { prefs ->
            prefs[COUNTERS_KEY] = jsonArray.toString()
        }
    }

    // Read and parse the list back into Counter objects
    val countersFlow: Flow<List<Counter>> = context.dataStore.data.map { prefs ->
        val jsonString = prefs[COUNTERS_KEY] ?: "[]"
        val array = JSONArray(jsonString)
        val list = mutableListOf<Counter>()
        for (i in 0 until array.length()) {
            val obj = array.getJSONObject(i)
            list.add(
                Counter(
                    id = obj.getLong("id"),
                    title = obj.getString("title"),
                    startDate = LocalDate.parse(obj.getString("startDate"))
                )
            )
        }
        list
    }
}
