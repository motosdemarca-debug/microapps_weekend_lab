package com.example.dayssince

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class DaysSinceWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // Para cada instancia del widget
        for (appWidgetId in appWidgetIds) {
            val views = RemoteViews(context.packageName, R.layout.widget_days_since)

            // Cargar el contador más reciente (sincrónico y breve para MVP)
            val topDays = runBlocking {
                try {
                    val ds = DataStoreManager(context)
                    val list = ds.countersFlow.first()
                    val latest = list.maxByOrNull { it.id }
                    latest?.daysSince?.toString() ?: "0"
                } catch (_: Exception) {
                    "0"
                }
            }

            views.setTextViewText(R.id.tvDays, topDays)
            views.setTextViewText(R.id.tvLabel, "days")

            // Tap abre la app
            val intent = Intent(context, MainActivity::class.java)
            val pending = PendingIntent.getActivity(
                context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.tvDays, pending)
            views.setOnClickPendingIntent(R.id.tvLabel, pending)

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }

    companion object {
        fun forceUpdate(context: Context) {
            val mgr = AppWidgetManager.getInstance(context)
            val ids = mgr.getAppWidgetIds(ComponentName(context, DaysSinceWidgetProvider::class.java))
            if (ids.isNotEmpty()) {
                AppWidgetProvider().onUpdate(context, mgr, ids)
            }
        }
    }
}
