package com.example.dayssince

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

class DaysSinceViewModel(application: Application) : AndroidViewModel(application) {

    private val store = DataStoreManager(application)

    private val _counters = MutableStateFlow<List<Counter>>(emptyList())
    val counters: StateFlow<List<Counter>> = _counters.asStateFlow()

    private var nextId = 1L

    init {
        // Carga inicial desde DataStore
        viewModelScope.launch {
            store.countersFlow.collect { list ->
                _counters.value = list
                // Recalcular nextId en base a lo cargado
                nextId = (list.maxOfOrNull { it.id } ?: 0L) + 1L
            }
        }
    }

    fun addCounter(title: String, startDate: LocalDate) {
        val trimmed = title.trim()
        if (trimmed.isEmpty()) return
        val updated = _counters.value + Counter(id = nextId++, title = trimmed, startDate = startDate)
        setAndPersist(updated)
    }

    fun resetCounter(id: Long) {
        val updated = _counters.value.map { c ->
            if (c.id == id) c.copy(startDate = LocalDate.now()) else c
        }
        setAndPersist(updated)
    }

    fun deleteCounter(id: Long) {
        val updated = _counters.value.filterNot { it.id == id }
        setAndPersist(updated)
    }

    // --- Persistencia ---

    private fun setAndPersist(list: List<Counter>) {
        _counters.value = list
        viewModelScope.launch { store.saveCounters(list) }
    }
}
