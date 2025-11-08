package com.example.dayssince

import java.time.LocalDate
import java.time.temporal.ChronoUnit

data class Counter(
    val id: Long,
    val title: String,
    val startDate: LocalDate
) {
    val daysSince: Long
        get() = ChronoUnit.DAYS.between(startDate, LocalDate.now())
}
