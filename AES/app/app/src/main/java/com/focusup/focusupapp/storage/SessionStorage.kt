package com.focusup.focusupapp.storage

import android.content.Context
import com.focusup.focusupapp.model.Account
import com.google.gson.Gson
import java.io.*
import com.google.gson.GsonBuilder
import java.time.LocalDate
import java.time.LocalTime

class SessionStorage {

    companion object {
        private const val SESSION_FILE = "current_session.json"
        private val gson: Gson = GsonBuilder()
            .registerTypeAdapter(LocalDate::class.java, LocalDateAdapter())
            .registerTypeAdapter(LocalTime::class.java, LocalTimeAdapter())
            .create()

        /**
         * Guarda la sesión actual en un archivo JSON
         */
        fun saveSession(context: Context, account: Account) {
            try {
                val file = File(context.filesDir, SESSION_FILE)
                val jsonString = gson.toJson(account)
                
                FileOutputStream(file).use { outputStream ->
                    outputStream.write(jsonString.toByteArray())
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        /**
         * Carga la sesión guardada desde el archivo JSON
         */
        fun loadSession(context: Context): Account? {
            return try {
                val file = File(context.filesDir, SESSION_FILE)
                if (!file.exists()) {
                    return null
                }
                
                FileInputStream(file).use { inputStream ->
                    val jsonString = inputStream.bufferedReader().use { it.readText() }
                    gson.fromJson(jsonString, Account::class.java)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

        /**
         * Elimina la sesión guardada (para logout)
         */
        fun clearSession(context: Context) {
            try {
                val file = File(context.filesDir, SESSION_FILE)
                if (file.exists()) {
                    file.delete()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        /**
         * Verifica si existe una sesión guardada
         */
        fun hasSavedSession(context: Context): Boolean {
            return try {
                val file = File(context.filesDir, SESSION_FILE)
                file.exists()
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }

        /**
         * Actualiza la sesión guardada con los datos más recientes de la cuenta
         */
        fun refreshSession(context: Context, updatedAccount: Account) {
            saveSession(context, updatedAccount)
        }

        /**
         * Obtiene la cuenta de la sesión actualizada desde el almacenamiento
         */
        fun getUpdatedSession(context: Context): Account? {
            val currentSession = loadSession(context)
            return currentSession?.let { session ->
                AccountStorage.getAccountById(context, session.id)
            }
        }
    }
}