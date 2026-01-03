package com.focusup.focusupapp.model

import com.focusup.focusupapp.ui.theme.ThemeIds

data class Account(
    val id: Int,
    val name: String,
    val email: String,
    val password: String, 
    val isPremium: Boolean,
    var points: Int = 0,
    var purchasedRewards: MutableList<Int> = mutableListOf(),
    val createdAt: String,
    var tasks: MutableList<Task> = mutableListOf(), // lista de tareas propias de la cuenta
    var selectedThemeId: Int = ThemeIds.LIGHT2
)