package com.focusup.focusupapp.storage

import android.content.Context

// Clase para manejar las preferencias relacionadas con el tutorial
object TutorialPreferences {
    private const val PREF_NAME = "focusup_prefs"
    private const val KEY_TUTORIAL_SHOWN = "tutorial_shown"

    // Verifica si el tutorial ya ha sido mostrado
    fun hasSeenTutorial(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_TUTORIAL_SHOWN, false)
    }

    // Marca el tutorial como mostrado
    fun setTutorialShown(context: Context) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(KEY_TUTORIAL_SHOWN, true).apply()
    }
}
