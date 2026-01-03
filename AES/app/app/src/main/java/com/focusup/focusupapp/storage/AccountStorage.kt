package com.focusup.focusupapp.storage

import android.content.Context
import com.focusup.focusupapp.model.Account
import com.focusup.focusupapp.model.Task
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.io.File
import java.time.LocalDate
import java.time.LocalTime

object AccountStorage {
    private const val MAX_TASKS_NON_PREMIUM = 10
    private const val ACCOUNTS_FILE = "accounts.json"
    private val gson: Gson = GsonBuilder()
        .registerTypeAdapter(LocalDate::class.java, LocalDateAdapter())
        .registerTypeAdapter(LocalTime::class.java, LocalTimeAdapter())
        .create()

    private fun getAccountsFile(context: Context): File {
        return File(context.filesDir, ACCOUNTS_FILE)
    }

    fun loadAccounts(context: Context): List<Account> {
        val file = getAccountsFile(context)
        if (!file.exists()) {
            return emptyList()
        }

        return try {
            val json = file.readText()
            val type = object : TypeToken<List<Account>>() {}.type
            gson.fromJson(json, type) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun saveAccounts(context: Context, accounts: List<Account>) {
        val file = getAccountsFile(context)
        val json = gson.toJson(accounts)
        file.writeText(json)
    }

    fun addAccount(context: Context, account: Account) {
        val accounts = loadAccounts(context).toMutableList()
        accounts.add(account)
        saveAccounts(context, accounts)
    }

    fun isEmailUsed(context: Context, email: String): Boolean {
        val accounts = loadAccounts(context)
        return accounts.any { it.email == email }
    }

    fun getNextId(context: Context): Int {
        val accounts = loadAccounts(context)
        return if (accounts.isEmpty()) 1 else accounts.maxOf { it.id } + 1
    }

    fun validateLogin(context: Context, email: String, password: String): Account? {
        val accounts = loadAccounts(context)
        return accounts.find { it.email == email && it.password == password }
    }

    // Función para dar puntos a una cuenta
    fun addPointsToAccount(context: Context, accountId: Int, points: Int): Account? {
        val accounts = loadAccounts(context).toMutableList()
        val accountIndex = accounts.indexOfFirst { it.id == accountId }
        if (accountIndex == -1) return null
        
        accounts[accountIndex].points += points
        saveAccounts(context, accounts)
        return accounts[accountIndex]
    }

    fun purchaseReward(context: Context, accountId: Int, rewardId: Int, cost: Int): Account? {
        val accounts = loadAccounts(context).toMutableList()
        val idx = accounts.indexOfFirst { it.id == accountId }
        if (idx < 0) return null
        val acc = accounts[idx]

        if (acc.points < cost) return null

        acc.points = (acc.points - cost).coerceAtLeast(0)

        if (!acc.purchasedRewards.contains(rewardId)) {
            acc.purchasedRewards.add(rewardId)
        }

        accounts[idx] = acc
        saveAccounts(context, accounts)
        return acc
    }

    // ========== MÉTODOS PARA MANEJAR TAREAS ==========

    /**
     * Agrega una tarea a una cuenta específica
     */
    fun addTaskToAccount(context: Context, accountId: Int, task: Task, isPremium: Boolean): Boolean {
        val accounts = loadAccounts(context).toMutableList()
        val accountIndex = accounts.indexOfFirst { it.id == accountId }
        if (accountIndex == -1) return false
        
        val account = accounts[accountIndex]
        
        // Verificar límite de tareas según si es premium o no
        val uncompletedTasksCount = account.tasks.count { !it.isCompleted }
        if (!isPremium && uncompletedTasksCount >= MAX_TASKS_NON_PREMIUM) {
            return false
        }
        account.tasks.add(task)
        // Mover las tareas completadas al final de la lista
        account.tasks = account.tasks.sortedWith(compareBy<Task> { it.isCompleted }).toMutableList()
        saveAccounts(context, accounts)
        return true
    }

    /**
     * Elimina una tarea de una cuenta específica
     */
    fun removeTaskFromAccount(context: Context, accountId: Int, taskId: Int) {
        val accounts = loadAccounts(context).toMutableList()
        val accountIndex = accounts.indexOfFirst { it.id == accountId }
        if (accountIndex == -1) return
        
        accounts[accountIndex].tasks.removeAll { it.id == taskId }
        saveAccounts(context, accounts)
    }

    /**
     * Elimina un paso de una tarea específica
     */
    fun removeStepFromTaskInAccount(context: Context, accountId: Int, taskId: Int, stepIndex: Int) {
        val accounts = loadAccounts(context).toMutableList()
        val accountIndex = accounts.indexOfFirst { it.id == accountId }
        if (accountIndex == -1) return
        
        val task = accounts[accountIndex].tasks.find { it.id == taskId } ?: return
        
        if (stepIndex in task.steps.indices) {
            task.steps.removeAt(stepIndex)
            saveAccounts(context, accounts)
        }
    }

    /**
     * Obtiene el siguiente ID disponible para una tarea en una cuenta
     */
    fun getNextTaskIdForAccount(context: Context, accountId: Int): Int {
        val accounts = loadAccounts(context)
        val account = accounts.find { it.id == accountId } ?: return 1
        
        return if (account.tasks.isEmpty()) {
            1
        } else {
            account.tasks.maxOf { it.id } + 1
        }
    }

    /**
     * Obtiene todas las tareas de una cuenta
     */
    fun getTasksForAccount(context: Context, accountId: Int): List<Task> {
        val accounts = loadAccounts(context)
        val account = accounts.find { it.id == accountId } ?: return emptyList()
        
        return account.tasks.toList()
    }

    /**
     * Actualiza una cuenta completa (útil para refrescar datos)
     */
    fun updateAccount(context: Context, updatedAccount: Account): Boolean {
        val accounts = loadAccounts(context).toMutableList()
        val accountIndex = accounts.indexOfFirst { it.id == updatedAccount.id }
        if (accountIndex == -1) return false
        
        accounts[accountIndex] = updatedAccount
        saveAccounts(context, accounts)
        return true
    }

    /**
     * Obtiene una cuenta por su ID
     */
    fun getAccountById(context: Context, accountId: Int): Account? {
        val accounts = loadAccounts(context)
        return accounts.find { it.id == accountId }
    }

    /**
     * Actualiza el estado de un paso en una tarea específica
     */
    fun updateStepInTask(context: Context, accountId: Int, taskId: Int, stepIndex: Int,isCompleted: Boolean) {
        val accounts = loadAccounts(context).toMutableList()
        val accountIndex = accounts.indexOfFirst { it.id == accountId }
        if (accountIndex == -1) return
        val task = accounts[accountIndex].tasks.find { it.id == taskId } ?: return
        if (stepIndex in task.steps.indices) {
            task.steps[stepIndex].isCompleted = isCompleted
            saveAccounts(context, accounts)
        }
    }

    /**
     * Actualiza una tarea específica en una cuenta
     */
    fun updateTaskOfAccount(context: Context, accountId: Int, updatedTask: Task) {
        val accounts = loadAccounts(context).toMutableList()
        val accountIndex = accounts.indexOfFirst { it.id == accountId }
        if (accountIndex == -1) return
        val account = accounts[accountIndex]
        val taskIndex = account.tasks.indexOfFirst { it.id == updatedTask.id }
        if (taskIndex == -1) return
        // Actualizar la tarea
        account.tasks[taskIndex] = updatedTask
        // Si la tarea esta completada, moverla al final de la lista
        if (updatedTask.isCompleted) {
            val completedTask = account.tasks.removeAt(taskIndex)
            account.tasks.add(completedTask)
        }
        saveAccounts(context, accounts)
    }

    /**
     * Descompleta una tarea específica en una cuenta
     */
    fun uncompleteTaskOfAccount(context: Context, accountId: Int, taskId: Int): Boolean {
        val accounts = loadAccounts(context).toMutableList()
        val accountIndex = accounts.indexOfFirst { it.id == accountId }
        if (accountIndex == -1) return false
        val account = accounts[accountIndex]
        val uncompletedTasksCount = account.tasks.count { !it.isCompleted }
        val isPremium = account.isPremium
        if (!isPremium && uncompletedTasksCount >= MAX_TASKS_NON_PREMIUM) {
            return false
        }
        val taskIndex = account.tasks.indexOfFirst { it.id == taskId }
        if (taskIndex == -1) return false
        val task = account.tasks[taskIndex]
        task.isCompleted = false
        // Mover la tarea al inicio de la lista
        val uncompletedTask = account.tasks.removeAt(taskIndex)
        account.tasks.add(0, uncompletedTask)
        saveAccounts(context, accounts)
        return true
    }
}