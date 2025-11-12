package com.example.dayssince

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.WorkManager
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.ListenableWorker.Result as WmResult
import java.time.Duration
import java.time.ZonedDateTime

class MidnightWidgetWorker(appContext: Context, params: WorkerParameters)
    : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): WmResult {
        DaysSinceWidgetProvider.forceUpdate(applicationContext)
        return WmResult.success()
    }

    companion object {
        private const val NAME = "DaysSinceMidnight"

        fun schedule(context: Context) {
            val request = PeriodicWorkRequestBuilder<MidnightWidgetWorker>(Duration.ofDays(1))
                .setInitialDelay(initialDelayToNextMidnight())
                .build()

            WorkManager.getInstance(context)
                .enqueueUniquePeriodicWork(
                    NAME,
                    ExistingPeriodicWorkPolicy.UPDATE,
                    request
                )
        }

        private fun initialDelayToNextMidnight(): Duration {
            val now = ZonedDateTime.now()
            val nextMidnight = now.toLocalDate().plusDays(1).atStartOfDay(now.zone)
            return Duration.between(now, nextMidnight)
        }
    }
}
