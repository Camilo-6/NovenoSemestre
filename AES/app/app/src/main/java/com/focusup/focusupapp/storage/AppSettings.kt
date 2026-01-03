package com.focusup.focusupapp.storage

import android.content.Context

// Clase para manejar las preferencias de la aplicacion
object AppSettings {
    private const val PREFS_NAME = "focusup_settings"
    private const val KEY_HOUR = "reminder_hour"
    private const val KEY_MINUTE = "reminder_minute"
    private const val KEY_THEME = "app_theme"

    // Guardar la hora y minuto del recordatorio diario
    fun saveReminderTime(context: Context, hour: Int, minute: Int) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().apply {
            putInt(KEY_HOUR, hour)
            putInt(KEY_MINUTE, minute)
            apply()
        }
    }

    // Obtener la hora del recordatorio diario
    fun getReminderHour(context: Context): Int {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getInt(KEY_HOUR, 9)
    }

    // Obtener el minuto del recordatorio diario
    fun getReminderMinute(context: Context): Int {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getInt(KEY_MINUTE, 0)
    }

    // Guardar el tema de la aplicacion
    fun saveTheme(context: Context, themeId: Int) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putInt(KEY_THEME, themeId).apply()
    }

    // Obtener el tema de la aplicacion
    fun getTheme(context: Context): Int {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getInt(KEY_THEME, 1) // 1 = LightColorScheme2
    }
}
