/*package com.focusup.focusupapp.ui

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.focusup.focusupapp.model.Task
import com.focusup.focusupapp.storage.TaskStorage
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*

@Composable
fun EditTaskDialog(
    context: Context,
    task: Task,
    onDismiss: () -> Unit,
    onSave: (Task) -> Unit
) {
    // Estados iniciales con los valores actuales de la tarea
    var title by remember { mutableStateOf(task.title) }
    var description by remember { mutableStateOf(task.description) }
    var date by remember { mutableStateOf(task.dueDate) }
    var time by remember { mutableStateOf(task.dueTime) }

    val localContext = LocalContext.current

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.background,
            tonalElevation = 4.dp,
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {

                Text(
                    text = "Editar tarea",
                    style = MaterialTheme.typography.titleLarge
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Campo de título
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Título") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Campo de descripción
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descripción") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Selector de fecha
                TextButton(
                    onClick = {
                        DatePickerDialog(
                            localContext,
                            { _, year, month, dayOfMonth ->
                                date = LocalDate.of(year, month + 1, dayOfMonth)
                            },
                            date.year,
                            date.monthValue - 1,
                            date.dayOfMonth
                        ).show()
                    }
                ) {
                    Text("Fecha: ${date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))}")
                }

                // Selector de hora
                TextButton(
                    onClick = {
                        TimePickerDialog(
                            localContext,
                            { _, hourOfDay, minute ->
                                time = LocalTime.of(hourOfDay, minute)
                            },
                            time.hour,
                            time.minute,
                            true
                        ).show()
                    }
                ) {
                    Text("Hora: ${time.format(DateTimeFormatter.ofPattern("HH:mm"))}")
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Botones inferiores
                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancelar")
                    }
                    Button(onClick = {
                        val updatedTask = task.copy(
                            title = title.ifBlank { "Tarea sin título" },
                            description = description.ifBlank { "Sin descripción" },
                            dueDate = date,
                            dueTime = time.withSecond(0).withNano(0)
                        )

                        // Actualizar tarea en almacenamiento
                        TaskStorage.updateTask(context, updatedTask)

                        // Enviar cambios de regreso
                        onSave(updatedTask)
                    }) {
                        Text("Guardar cambios")
                    }
                }
            }
        }
    }
}*/