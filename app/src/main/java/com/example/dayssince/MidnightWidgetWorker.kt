package com.example.dayssince

import android.content.Context
import androidx.work.*
import java.time.Duration
import java.time.ZonedDateTime
import java.util.concurrent.TimeUnit

class MidnightWidgetWorker(appContext: Context, params: WorkerParameters)
    : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        DaysSinceWidgetProvider.forceUpdate(applicationContext)
        return Result.success()
    }

    companion object {
        private const val NAME = "DaysSinceMidnight"

        fun schedule(context: Context) {
            val now = ZonedDateTime.now()
            val nextMidnight = now.toLocalDate().plusDays(1).atStartOfDay(now.zone)
            val delayMin = Duration.between(now, nextMidnight).toMinutes()

            val req = PeriodicWorkRequestBuilder<MidnightWidgetWorker>(24, TimeUnit.HOURS)
                .setInitialDelay(delayMin, TimeUnit.MINUTES)
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                NAME, ExistingPeriodicWorkPolicy.UPDATE, req
            )
        }
    }
}
