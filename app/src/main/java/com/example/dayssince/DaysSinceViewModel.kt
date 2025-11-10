package com.example.dayssince

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.LocalDate

class DaysSinceViewModel(app: Application) : AndroidViewModel(app) {

    private val dataStore = DataStoreManager(app)

    private val _counters = MutableStateFlow<List<Counter>>(emptyList())
    val counters: StateFlow<List<Counter>> = _counters.asStateFlow()

    init {
        // Cargar y observar los contadores guardados
        viewModelScope.launch {
            dataStore.countersFlow.collectLatest { stored ->
                _counters.value = stored
            }
        }
    }

    /** Crea un nuevo contador. */
    fun addCounter(title: String, startDate: LocalDate) {
        val newItem = Counter(
            id = System.currentTimeMillis(), // id simple suficiente para una microapp
            title = title.trim(),
            startDate = startDate
        )
        val updated = _counters.value.toMutableList().apply { add(0, newItem) }
        save(updated)
    }

    /** Resetea la fecha de inicio de un contador existente. */
    fun resetCounter(id: Long, newStartDate: LocalDate = LocalDate.now()) {
        val updated = _counters.value.map { c ->
            if (c.id == id) c.copy(startDate = newStartDate) else c
        }
        save(updated)
    }

    /** Borra un contador por id. */
    fun deleteCounter(id: Long) {
        val updated = _counters.value.filterNot { it.id == id }
        save(updated)
    }

    /** Guarda en DataStore y fuerza el repintado del widget. */
    private fun save(list: List<Counter>) {
        _counters.value = list
        viewModelScope.launch {
            dataStore.saveCounters(list)
            // 🔔 tras guardar, refrescamos el widget
            DaysSinceWidgetProvider.forceUpdate(getApplication())
        }
    }
}
