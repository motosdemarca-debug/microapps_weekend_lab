package com.example.dayssince

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate

class DaysSinceViewModel(app: Application) : AndroidViewModel(app) {

    private val dataStore = DataStoreManager(app)

    private val _counters = MutableStateFlow<List<Counter>>(emptyList())
    val counters: StateFlow<List<Counter>> = _counters.asStateFlow()

    private var nextId = 1L

    init {
        // Load from DataStore when ViewModel starts
        viewModelScope.launch {
            dataStore.countersFlow.collect { saved ->
                _counters.value = saved
                nextId = (saved.maxOfOrNull { it.id } ?: 0L) + 1
            }
        }
    }

    fun addCounter(title: String, startDate: LocalDate) {
        val newCounter = Counter(nextId++, title.trim(), startDate)
        val updated = _counters.value + newCounter
        save(updated)
    }

    fun resetCounter(id: Long) {
        val updated = _counters.value.map { c ->
            if (c.id == id) c.copy(startDate = LocalDate.now()) else c
        }
        save(updated)
    }

    fun deleteCounter(id: Long) {
        val updated = _counters.value.filterNot { it.id == id }
        save(updated)
    }

    private fun save(list: List<Counter>) {
        _counters.value = list
        viewModelScope.launch {
            dataStore.saveCounters(list)
        }
    }
}
