package com.example.dayssince

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.temporal.ChronoUnit

/**
 * Widget clásico que muestra el contador principal desde DataStore.
 */
class DaysSinceWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)

        CoroutineScope(Dispatchers.IO).launch {
            val dataStore = DataStoreManager(context)
            val counters = try {
                dataStore.countersFlow.first() // obtiene la lista actual
            } catch (e: Exception) {
                emptyList<Counter>()
            }

            val mainCounter = counters.firstOrNull()

            // Actualiza cada instancia del widget
            for (widgetId in appWidgetIds) {
                val views = RemoteViews(context.packageName, R.layout.widget_days_since)

                if (mainCounter != null) {
                    val days = ChronoUnit.DAYS.between(mainCounter.startDate, LocalDate.now())
                    views.setTextViewText(R.id.tvDays, days.toString())
                    views.setTextViewText(R.id.tvLabel, mainCounter.title)
                } else {
                    views.setTextViewText(R.id.tvDays, "0")
                    views.setTextViewText(R.id.tvLabel, "Sin contador")
                }

                // Tap → abre la app
                val intent = Intent(context, MainActivity::class.java)
                val pendingIntent = PendingIntent.getActivity(
                    context,
                    0,
                    intent,
                    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                )
                views.setOnClickPendingIntent(R.id.root, pendingIntent)

                appWidgetManager.updateAppWidget(widgetId, views)
            }
        }
    }

    companion object {
        /**
         * Llamar desde el ViewModel (tras guardar en DataStore) para refrescar el widget.
         */
        fun forceUpdate(context: Context) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val component = ComponentName(context, DaysSinceWidgetProvider::class.java)
            val widgetIds = appWidgetManager.getAppWidgetIds(component)
            if (widgetIds.isNotEmpty()) {
                val intent = Intent(context, DaysSinceWidgetProvider::class.java).apply {
                    action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
                    putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, widgetIds)
                }
                context.sendBroadcast(intent)
            }
        }
    }
}
