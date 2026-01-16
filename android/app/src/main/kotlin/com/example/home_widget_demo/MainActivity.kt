package com.example.home_widget_demo

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent

import com.example.home_widget_demo.widget.NewsWidget
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel

class MainActivity : FlutterActivity() {
    private val channel = "com.example.home_widget/widget"
    private val PREFS_NAME = "flutter_widget_prefs"

    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)

        MethodChannel(
            flutterEngine.dartExecutor.binaryMessenger,
            channel
        ).setMethodCallHandler { call, result ->
            when (call.method) {
                "updateWidget" -> {
                    val name = call.argument<String>("name") ?: "No name saved"
                    val lastUpdated = call.argument<String>("lastUpdated") ?: ""
                    val message = call.argument<String>("message") ?: "Enter your name"

                    updateHomeScreenWidget(name, lastUpdated, message)
                    result.success(null)
                }
                "getWidgetData" -> {
                    val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                    val name = prefs.getString("saved_name", "No name saved")
                    val lastUpdated = prefs.getString("last_updated", "")
                    val message = prefs.getString("widget_message", "Enter your name")

                    val data = mapOf(
                        "name" to name,
                        "lastUpdated" to lastUpdated,
                        "message" to message,
                    )
                    result.success(data)

                }
                else -> result.notImplemented()
            }
        }
    }

    private fun updateHomeScreenWidget(name: String, lastUpdated: String, message: String) {

        // Save data to SharedPreferences
        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        with(prefs.edit()) {
            putString("saved_name", name)
            putString("last_updated", lastUpdated)
            putString("widget_message", message)
            apply()
        }

        // Update the widget

        val intent = Intent (this, NewsWidget::class.java)
        intent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE

        val ids = AppWidgetManager.getInstance(application)
            .getAppWidgetIds(ComponentName(application, NewsWidget::class.java))
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,ids)
        sendBroadcast(intent)

        println("Android widget update widget")

    }
}

