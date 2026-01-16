package com.example.home_widget_demo.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.example.home_widget_demo.MainActivity
import com.example.home_widget_demo.R

class FlutterWidgetProvider : AppWidgetProvider() {
    companion object {
        private const val PREFS_NAME = "flutter_home_widget"

        fun updateWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
            // Get data from SharedPreference
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val name = prefs.getString("saved_named", "LâmBaoBao") ?: "No name saved"
            val lastUpdated =
                prefs.getString("last_updated", "No last updated") ?: "No last updated"

            val message = prefs.getString("widget_message", "Enter your message") ?:"Enter your message"

            val view = RemoteViews(context.packageName, R.layout.flutter_widget)

            view.setTextViewText(R.id.widget_title, "Name Widget")
            view.setTextViewText(R.id.widget_name, "Name: $name")
            view.setTextViewText(R.id.widget_message, message)

            if (lastUpdated.isNotEmpty()) {
                view.setTextViewText(R.id.widget_time, "Update $lastUpdated")
            }
            else {
                view.setTextViewText(R.id.widget_time, "")
            }
            val intent = Intent(context, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            val pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            view.setOnClickPendingIntent(R.id.widget_container, pendingIntent)

            appWidgetManager.updateAppWidget(appWidgetId,view)
        }

        fun onUpdate (
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: IntArray
        ){
            for (appWidgetId in appWidgetId) {
                updateWidget(context,appWidgetManager,appWidgetId)
            }
        }
        fun onReceive(context: Context, intent: Intent){
//            super.onReceive(context,intent)

            if (intent.action == AppWidgetManager.ACTION_APPWIDGET_UPDATE){
                val appWidgetManager = AppWidgetManager.getInstance(context)
                val componentName = ComponentName(context, FlutterWidgetProvider::class.java)
                val appWidgetIds = appWidgetManager.getAppWidgetIds(componentName)

                for (appWidgetId in appWidgetIds) {
                    updateWidget(context,appWidgetManager,appWidgetId)
                }
            }
        }

        fun onEnabled (context: Context) {
            println("Android widget enabled")
        }
        fun onDisabled (context: Context){
            println("Android widget disabled")
        }
        fun onDeleted (context: Context, appWidgetIds: IntArray){
            println("Android widget deleted: ${appWidgetIds.size}")
        }

    }
}