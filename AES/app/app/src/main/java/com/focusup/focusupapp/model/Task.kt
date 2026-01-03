package com.focusup.focusupapp.model

import java.time.LocalDate
import java.time.LocalTime

// Clase para representar una tarea
data class Task(
    // Titulo
    val title: String,
    // Descripcion
    val description: String,
    // Fecha de vencimiento, ejemplo: 2023-12-31
    val dueDate: LocalDate,
    // Hora de vencimiento, ejemplo: 14:30
    val dueTime: LocalTime,
    // Dificultad, de 1 a 5
    val difficulty: Int,
    // Pasos para completar la tarea, para poder poner actividades
    var steps: MutableList<Step>,
    // ID
    val id: Int,
    // Indica si la tarea esta completada
    var isCompleted: Boolean = false
)