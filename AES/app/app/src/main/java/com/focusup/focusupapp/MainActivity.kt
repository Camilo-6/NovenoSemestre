package com.focusup.focusupapp

import android.Manifest
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.graphics.Color
import com.focusup.focusupapp.ui.theme.FocusUpTheme
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.material3.Surface
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.foundation.BorderStroke
import android.util.Patterns
import android.widget.Toast

import java.time.LocalDate
import java.time.YearMonth
import java.util.Locale
import java.time.format.DateTimeFormatter
import java.time.LocalTime

import com.focusup.focusupapp.model.Task
//import com.example.focusup.model.TasksProvider
import com.focusup.focusupapp.ui.theme.SpecialRed
import com.focusup.focusupapp.ui.theme.SpecialRed2
import com.focusup.focusupapp.ui.theme.SpecialOrange
import com.focusup.focusupapp.ui.theme.SpecialOrange2
import com.focusup.focusupapp.ui.theme.SpecialYellow
import com.focusup.focusupapp.ui.theme.SpecialYellow2
import com.focusup.focusupapp.ui.theme.SpecialGreen
import com.focusup.focusupapp.ui.theme.SpecialGreen2
import com.focusup.focusupapp.ui.theme.SpecialBlue
import com.focusup.focusupapp.ui.theme.SpecialBlue2
import com.focusup.focusupapp.ui.theme.SpecialGray
import com.focusup.focusupapp.ui.theme.SpecialGray2
import com.focusup.focusupapp.ui.theme.Black

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Help
import java.util.*
import java.time.Duration


import java.time.format.DateTimeFormatter.ofPattern

import com.focusup.focusupapp.model.Account
import com.focusup.focusupapp.storage.AccountStorage
import java.time.LocalDateTime

import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import com.focusup.focusupapp.storage.AppSettings
import com.focusup.focusupapp.ui.theme.LocalThemeId
import androidx.compose.foundation.interaction.MutableInteractionSource

import com.focusup.focusupapp.storage.TutorialPreferences
import com.focusup.focusupapp.storage.SessionStorage
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.size
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import com.focusup.focusupapp.ui.theme.rewardThemeColors

import com.focusup.focusupapp.ui.theme.ThemeIds
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.filled.Delete
import androidx.compose.ui.text.style.TextDecoration
import com.focusup.focusupapp.model.Step

import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.material3.Text
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue


// Crear canal de notificacion
@RequiresApi(Build.VERSION_CODES.O)
private fun createNotificationChannel(context: Context) {
    val channel = NotificationChannel(
        "TASK_REMINDER_CHANNEL",
        "Recordatorios de tareas",
        NotificationManager.IMPORTANCE_HIGH
    ).apply {
        description = "Canal para recordatorios diarios de FocusUp"
    }
    val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    manager.createNotificationChannel(channel)
}

// Recordatorio diario
@RequiresApi(Build.VERSION_CODES.O)
fun scheduleDailyReminder(context: Context) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val intent = Intent(context, ReminderReceiver::class.java)
    val pendingIntent = PendingIntent.getBroadcast(
        context,
        0,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    // Obtenemos la hora y minuto guardados en preferencias
    val hour = AppSettings.getReminderHour(context)
    val minute = AppSettings.getReminderMinute(context)

    // Configura la hora guardada en preferencias
    val calendar = Calendar.getInstance().apply {
        timeInMillis = System.currentTimeMillis()
        set(Calendar.HOUR_OF_DAY, hour)
        set(Calendar.MINUTE, minute)
        set(Calendar.SECOND, 0)
    }
    // En caso de que la hora ya haya pasado, programa para el dia siguiente
    if (calendar.timeInMillis < System.currentTimeMillis()) {
        calendar.add(Calendar.DAY_OF_YEAR, 1)
    }
    alarmManager.setRepeating(
        AlarmManager.RTC_WAKEUP,
        calendar.timeInMillis,
        AlarmManager.INTERVAL_DAY,
        pendingIntent
    )
}

@RequiresApi(Build.VERSION_CODES.O)
val dateFormatter = ofPattern("dd/MM/yyyy")
@RequiresApi(Build.VERSION_CODES.O)
val timeFormatter = ofPattern("HH:mm")

@Composable
fun isPortrait(): Boolean {
    val configuration = LocalConfiguration.current
    return configuration.orientation == Configuration.ORIENTATION_PORTRAIT
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 1)
        }
        createNotificationChannel(this)
        scheduleDailyReminder(this)

        val defaultTheme = AppSettings.getTheme(this)

        val sessionAccount = SessionStorage.getUpdatedSession(this)

        val initialTheme = sessionAccount?.selectedThemeId ?: defaultTheme

        val savedTheme = AppSettings.getTheme(this)
        setContent {
            val themeState = remember { mutableStateOf(initialTheme) }
            CompositionLocalProvider(LocalThemeId provides themeState) {
                FocusUpTheme {
                    CalendarScreen(context = this)
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun CalendarScreen(context: Context) {
    // Cargar tareas desde el almacenamiento
    val tasksList = remember {
        mutableStateListOf<Task>()
    }

    var showLoginDialog by remember { mutableStateOf(false) }

    // estados para el login
    var loggedIn by remember { mutableStateOf(false) }
    var isPremium by remember { mutableStateOf(false) }
    var currentAccount by remember { mutableStateOf<Account?>(null) }

    val today = LocalDate.now()
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }
    val daysOfMonth: List<LocalDate> =
        (1..currentMonth.lengthOfMonth()).map { currentMonth.atDay(it) }

    val firstDayOfMonth = currentMonth.atDay(1)
    val firstDayOfWeek = firstDayOfMonth.dayOfWeek.value // 1 = lunes, 7 = domingo (segun java.time)
    val daysInMonth = currentMonth.lengthOfMonth()
    val daysGrid = mutableListOf<LocalDate?>()
    // Aniade nulls hasta el primer día de la semana
    repeat(firstDayOfWeek - 1) { daysGrid.add(null) }
    // Aniade los días reales
    for (day in 1..daysInMonth) {
        daysGrid.add(currentMonth.atDay(day))
    }

    val monthFormatter = ofPattern("MMMM yyyy", Locale("es", "ES"))
    val monthName = currentMonth.format(monthFormatter).replaceFirstChar {
        if (it.isLowerCase()) it.titlecase(Locale("es", "ES")) else it.toString()
    }

    val tasks = tasksList

    val themeState = LocalThemeId.current

    val tasksByDay: Map<LocalDate, List<Task>> =
        daysOfMonth.associateWith { day ->
            tasks.filter { it.dueDate.year == day.year && it.dueDate.month == day.month && it.dueDate.dayOfMonth == day.dayOfMonth }
        }

    var showTaskDialog by remember { mutableStateOf(false) }
    var selectedTask by remember { mutableStateOf<Task?>(null) }
    var showTaskCompletedDialog by remember { mutableStateOf(false) }
    var taskToEdit by remember { mutableStateOf<Task?>(null) }

    var showAddTaskDialog by remember { mutableStateOf(false) }
    var showTaskCreatedDialog by remember { mutableStateOf(false) }
    var showTaskNotCreatedDialogNonPremium by remember { mutableStateOf(false) }
    var showTaskNotCreatedDialogPremium by remember { mutableStateOf(false) }

    var showAddAccountDialog by remember { mutableStateOf(false) }
    var showAccountCreatedDialog by remember { mutableStateOf(false) }

    var showUserManualDialog by remember { mutableStateOf(false) }

    var lastPointsEarned by remember { mutableStateOf(0) }

    var showSettingsDialog by remember { mutableStateOf(false) }

    var showRewards by remember { mutableStateOf(false) }
    var showLoginForStore by remember { mutableStateOf(false) }

    var showLoginRequiredForTask by remember { mutableStateOf(false) }

    // Mostrar tutorial si no se ha visto antes
    LaunchedEffect(Unit) {
        // cargando sesión al iniciar
        val savedSession = SessionStorage.loadSession(context)
        if (savedSession != null) {
            // Obtener la cuenta actualizada con las tareas más recientes
            val updatedAccount = SessionStorage.getUpdatedSession(context) ?: savedSession
            currentAccount = updatedAccount
            loggedIn = true
            isPremium = updatedAccount.isPremium
            
            // Cargar tareas de la cuenta
            tasksList.clear()
            tasksList.addAll(AccountStorage.getTasksForAccount(context, updatedAccount.id))
        }

        val seen = TutorialPreferences.hasSeenTutorial(context)
        if (!seen) {
            showUserManualDialog = true
            TutorialPreferences.setTutorialShown(context)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            "FocusUp",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        IconButton(
                            onClick = { showUserManualDialog = true },
                            modifier = Modifier.size(20.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Help,
                                contentDescription = "Ver tutorial",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        IconButton(onClick = { showSettingsDialog = true }) {
                            Icon(
                                imageVector = Icons.Filled.Settings,
                                contentDescription = "Configuración",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                actions = {
                    if (loggedIn) {

                    BadgedBox(
                        badge = {
                            if ((currentAccount?.points ?: 0) > 0) {
                                Badge { Text((currentAccount?.points ?: 0).toString()) }
                            }
                        }
                    ) {
                        IconButton(
                            onClick = {
                                if (currentAccount == null) {
                                    showLoginForStore = true
                                } else {
                                    showRewards = true
                                }
                            },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ShoppingCart,
                                contentDescription = "Recompensas"
                            )
                        }
                    }
                    }
                    Spacer(modifier = Modifier.width(8.dp))

                    // Si el usuario esta logueado, mostrar boton de perfil
                    if (loggedIn) {
                        var expanded by remember { mutableStateOf(false) }
                        Box(
                            modifier = Modifier
                                .wrapContentSize(Alignment.TopEnd)
                        ) {
                            IconButton(onClick = { expanded = true }) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = "Menú de usuario"
                                )
                            }
                            DropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 12.dp)
                                ) {
                                    Text("Hola, ${currentAccount?.name ?: "Usuario"}")
                                }
                                DropdownMenuItem(
                                    text = { Text("Recompensas, ${currentAccount?.points ?: 0}") },
                                    onClick = {
                                        showRewards = true
                                        expanded = false
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Cerrar sesión") },
                                    onClick = {
                                        SessionStorage.clearSession(context) // limpiando sesión al hacer logOut
                                        loggedIn = false
                                        isPremium = false
                                        currentAccount = null
                                        tasksList.clear()
                                        expanded = false

                                        val defaultTheme = AppSettings.getTheme(context)
                                        themeState.value = defaultTheme

                                    }
                                )
                            }
                        }
                    }
                    // Si el usuario no esta logueado, mostrar botones de login y registro
                    else {
                        TextButton(onClick = { showLoginDialog = true }) {
                            Text(
                                "Iniciar sesión",
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        TextButton(onClick = { showAddAccountDialog = true }) {
                            Text("Registrarse", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    titleContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    actionIconContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(10.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        "© 2025 FocusUp",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    /*
                    IconButton(
                        onClick = { showUserManualDialog = true },
                        modifier = Modifier.size(18.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Help,
                            contentDescription = "Ver tutorial",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    */
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Boton arriba
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Button(onClick = { showAddTaskDialog = true }) {
                    Text("+ Agregar tarea")
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(onClick = { currentMonth = currentMonth.minusMonths(1) }) {
                    Text("<")
                }
                Text(
                    text = monthName,
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Button(onClick = { currentMonth = currentMonth.plusMonths(1) }) {
                    Text(">")
                }
            }
            Spacer(modifier = Modifier.height(15.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                listOf("Lun", "Mar", "Mié", "Jue", "Vie", "Sáb", "Dom").forEach { dayName ->
                    Text(
                        dayName,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
            Spacer(modifier = Modifier.height(15.dp))

            // Grid de dias
            LazyVerticalGrid(
                columns = GridCells.Fixed(7), // 7 columnas para los dias de la semana
                verticalArrangement = Arrangement.spacedBy(15.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxHeight()
            ) {
                items(daysGrid) { date ->
                    if (date != null) {
                        val isToday = date == today
                        Card(
                            modifier = Modifier.height(100.dp)
                            .border(
                            shape = RoundedCornerShape(10.dp),
                        width = if (isToday) 3.dp else 0.dp,
                        color = if (isToday) MaterialTheme.colorScheme.surfaceVariant else Color.Transparent,
                        ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            )
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                // Numero del dia
                                Text(
                                    date.dayOfMonth.toString(),
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onBackground
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                // Mostrar tareas si existen
                                val tasksOfDay = tasksByDay[date] ?: emptyList()
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .verticalScroll(rememberScrollState())
                                ) {
                                    tasksOfDay.forEach { task ->
                                        Surface(
                                            tonalElevation = 0.dp,
                                            shape = RoundedCornerShape(6.dp),
                                            color = GetTaskColor(task.difficulty, task.isCompleted),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 2.dp)
                                                .clickable {
                                                    selectedTask = task
                                                    showTaskDialog = true
                                                }
                                        ) {
                                            Column(modifier = Modifier.padding(4.dp)) {
                                                Text(
                                                    task.title,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 10.sp,
                                                    color = MaterialTheme.colorScheme.onSecondary,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                                Text(
                                                    task.dueTime.format(timeFormatter),
                                                    style = MaterialTheme.typography.bodySmall.copy(
                                                        fontSize = 10.sp
                                                    ),
                                                    color = MaterialTheme.colorScheme.onSecondary,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    } else {

                    }
                }
            }

            if (showTaskDialog && selectedTask != null) {
                AlertDialog(
                    onDismissRequest = { showTaskDialog = false },
                    title = {
                        Text(
                            text = selectedTask!!.title + getStatus(selectedTask!!.isCompleted),
                            color = MaterialTheme.colorScheme.onSecondary
                        )
                    },
                    text = {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                "Descripción: ${selectedTask!!.description}",
                                color = MaterialTheme.colorScheme.onSecondary
                            )
                            Text(
                                "Fecha: ${selectedTask!!.dueDate.format(dateFormatter)}",
                                color = MaterialTheme.colorScheme.onSecondary
                            )
                            Text(
                                "Hora: ${selectedTask!!.dueTime.format(timeFormatter)}",
                                color = MaterialTheme.colorScheme.onSecondary
                            )
                            Text(
                                "Dificultad: ${selectedTask!!.difficulty}/5",
                                color = MaterialTheme.colorScheme.onSecondary
                            )
            
                            // ---------------------
                            //   SECCIÓN DE PASOS
                            // ---------------------
                            if (selectedTask!!.steps.isNotEmpty()) {
                                Text(
                                    "Pasos:",
                                    color = MaterialTheme.colorScheme.onSecondary
                                )
            
                                selectedTask!!.steps.forEachIndexed { index, step ->
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
            
                                        // Checkbox para marcar/desmarcar si la tarea no esta completada
                                        if (!selectedTask!!.isCompleted) {
                                            Checkbox(
                                                checked = step.isCompleted,
                                                onCheckedChange = { isChecked ->
                                                    step.isCompleted = isChecked
                
                                                    if (currentAccount != null) {
                                                        AccountStorage.updateStepInTask(
                                                            context,
                                                            currentAccount!!.id,
                                                            selectedTask!!.id,
                                                            index,
                                                            isChecked
                                                        )
                
                                                        val updatedAccount = SessionStorage.getUpdatedSession(context)
                                                        if (updatedAccount != null) {
                                                            currentAccount = updatedAccount
                                                            SessionStorage.refreshSession(context, updatedAccount)
                                                        }
                
                                                        val updatedTasks = AccountStorage.getTasksForAccount(context, currentAccount!!.id)
                                                        tasksList.clear()
                                                        tasksList.addAll(updatedTasks)
                                                        selectedTask = updatedTasks.find { it.id == selectedTask!!.id }
                                                    }
                                                },
                                                colors = CheckboxDefaults.colors(
                                                    checkmarkColor = Black,
                                                    uncheckedColor = Black,
                                                    checkedColor = GetTaskColorText(selectedTask!!.difficulty, selectedTask!!.isCompleted)
                                                )
                                            )
                                        }
            
                                        // Texto (con tachado si esta completado)
                                        Text(
                                            text = "${step.text}",
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                textDecoration = if (step.isCompleted)
                                                    TextDecoration.LineThrough
                                                else
                                                    TextDecoration.None
                                            ),
                                            color = MaterialTheme.colorScheme.onSecondary,
                                            modifier = Modifier.weight(1f)
                                        )
            
                                        // Botón para borrar el paso si la tarea no esta completada
                                        if (!selectedTask!!.isCompleted) {
                                            IconButton(
                                                onClick = {
                                                    if (currentAccount != null) {
                                                        AccountStorage.removeStepFromTaskInAccount(
                                                            context,
                                                            currentAccount!!.id,
                                                            selectedTask!!.id,
                                                            index
                                                        )
                
                                                        val updatedAccount = SessionStorage.getUpdatedSession(context)
                                                        if (updatedAccount != null) {
                                                            currentAccount = updatedAccount
                                                            SessionStorage.refreshSession(context, updatedAccount)
                                                        }
                
                                                        val updatedTasks = AccountStorage.getTasksForAccount(context, currentAccount!!.id)
                                                        tasksList.clear()
                                                        tasksList.addAll(updatedTasks)
                                                        selectedTask = updatedTasks.find { it.id == selectedTask!!.id }
                                                    }
                                                }
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Delete,
                                                    contentDescription = "Eliminar paso",
                                                    tint = MaterialTheme.colorScheme.onSecondary
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    },
                    confirmButton = {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Button(
                                onClick = {
                                    // Si la tarea ya estaba completada, descompletarla
                                    if (selectedTask!!.isCompleted) {
                                        val result = AccountStorage.uncompleteTaskOfAccount(context, currentAccount!!.id, selectedTask!!.id)
                                        if (!result) {
                                            selectedTask = null
                                            showTaskDialog = false
                                            if (isPremium) {
                                                showTaskNotCreatedDialogPremium = true
                                            } else {
                                                showTaskNotCreatedDialogNonPremium = true
                                            }
                                            return@Button
                                        }
                                        val updatedAccount = SessionStorage.getUpdatedSession(context)
                                        if (updatedAccount != null) {
                                            currentAccount = updatedAccount
                                            SessionStorage.refreshSession(context, updatedAccount)
                                        }
                                        // Actualizar la lista de tareas en la UI
                                        val updatedTasks = AccountStorage.getTasksForAccount(context, currentAccount!!.id)
                                        tasksList.clear()
                                        tasksList.addAll(updatedTasks)

                                        selectedTask = null
                                        showTaskDialog = false
                                        return@Button
                                    }
                                    showTaskDialog = false
                                    lastPointsEarned = 0
                
                                    // Obtener puntos si el usuario está logueado
                                    if (loggedIn && currentAccount != null) {
                                        val pointsEarned = CalculatePointsForTask(selectedTask!!)
                                        lastPointsEarned = pointsEarned
                
                                        val updatedAccount = AccountStorage.addPointsToAccount(
                                            context,
                                            currentAccount!!.id,
                                            pointsEarned
                                        )
                                        if (updatedAccount != null) {
                                            currentAccount = updatedAccount
                                            SessionStorage.refreshSession(context, updatedAccount)
                                        }
                                    }
                                    // Marcar tarea como completa en lugar de eliminarla
                                    selectedTask?.let { task ->
                                        task.isCompleted = true
                                        // Mover al final de la lista
                                        // Marcar tarea como completa y moverla al final de la lista si hay otras incompletas
                                        if (currentAccount != null) {
                                            AccountStorage.updateTaskOfAccount(context, currentAccount!!.id, task)

                                            val updatedAccount = SessionStorage.getUpdatedSession(context)
                                            if (updatedAccount != null) {
                                                currentAccount = updatedAccount
                                                SessionStorage.refreshSession(context, updatedAccount)
                                            }
                                        }
                                        // Actualizar la lista de tareas en la UI
                                        val updatedTasks = AccountStorage.getTasksForAccount(context, currentAccount!!.id)
                                        tasksList.clear()
                                        tasksList.addAll(updatedTasks)
                                    }
                                    selectedTask = null
                                    showTaskCompletedDialog = true
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = GetTaskColorText(selectedTask!!.difficulty, selectedTask!!.isCompleted),
                                    contentColor = MaterialTheme.colorScheme.onSecondary
                                )
                            ) {
                                Text(getCompleteButtonText(selectedTask!!.isCompleted))
                            }
                        }
                    },
                    dismissButton = {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TextButton(
                                onClick = {
                                    showTaskDialog = false
                                    // Si la tarea esta completada, eliminarla
                                    if (selectedTask!!.isCompleted) {
                                        if (currentAccount != null) {
                                            AccountStorage.removeTaskFromAccount(context, currentAccount!!.id, selectedTask!!.id)
                                            val updatedAccount = SessionStorage.getUpdatedSession(context)
                                            if (updatedAccount != null) {
                                                currentAccount = updatedAccount
                                                SessionStorage.refreshSession(context, updatedAccount)
                                            }
                                            val updatedTasks = AccountStorage.getTasksForAccount(context, currentAccount!!.id)
                                            tasksList.clear()
                                            tasksList.addAll(updatedTasks)
                                        }
                                        selectedTask = null
                                        return@TextButton
                                    }
                                    taskToEdit = selectedTask
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = GetTaskColorText(selectedTask!!.difficulty, selectedTask!!.isCompleted),
                                    contentColor = MaterialTheme.colorScheme.onSecondary
                                    )
                            ) {
                                Text(getEditButtonText(selectedTask!!.isCompleted))
                            }
                            Button(
                                onClick = { showTaskDialog = false },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = GetTaskColorText(selectedTask!!.difficulty, selectedTask!!.isCompleted),
                                    contentColor = MaterialTheme.colorScheme.onSecondary
                                )
                            ) {
                                Text("Cerrar")
                            }
                        }
                    },
                    containerColor = GetTaskColor(selectedTask!!.difficulty, selectedTask!!.isCompleted)
                )
            }
            
            if (showTaskCompletedDialog) {
                TaskCompletedDialog(
                    pointsEarned = lastPointsEarned,
                    onDismiss = { showTaskCompletedDialog = false }
                )
            }
            
            if (showAddTaskDialog) {
                AddTaskDialog(
                    onDismiss = { showAddTaskDialog = false },
                    onAddTask = { newTask ->
                        if (currentAccount == null) {
                            showLoginRequiredForTask = true
                            showAddTaskDialog = false
                            return@AddTaskDialog
                        }
            
                        val result = AccountStorage.addTaskToAccount(context, currentAccount!!.id, newTask, isPremium)
            
                        if (result) {
                            showTaskCreatedDialog = true
                            // Actualizar la sesión y las tareas
                            val updatedAccount = SessionStorage.getUpdatedSession(context)
                            if (updatedAccount != null) {
                                currentAccount = updatedAccount
                                SessionStorage.refreshSession(context, updatedAccount)
                            }
                            // Refrescar la lista de tareas
                            val updatedTasks = AccountStorage.getTasksForAccount(context, currentAccount!!.id)
                            tasksList.clear()
                            tasksList.addAll(updatedTasks)
                        } else {
                            if (isPremium) {
                                showTaskNotCreatedDialogPremium = true
                            } else {
                                showTaskNotCreatedDialogNonPremium = true
                            }
                        }
                        showAddTaskDialog = false
                    },
                    nextId = if (currentAccount != null) AccountStorage.getNextTaskIdForAccount(context, currentAccount!!.id) else 1
                )
            }

            if (showTaskCreatedDialog) {
                TaskCreatedDialog(
                    onDismiss = { showTaskCreatedDialog = false }
                )
            }

            if (showTaskNotCreatedDialogNonPremium) {
                TaskNotCreatedDialogNonPremium(
                    onDismiss = { showTaskNotCreatedDialogNonPremium = false }
                )
            }

            if (showTaskNotCreatedDialogPremium) {
                TaskNotCreatedDialogPremium(
                    onDismiss = { showTaskNotCreatedDialogPremium = false }
                )
            }

            if (showAddAccountDialog) {
                AddAccountDialog(
                    onDismiss = {
                        showAddAccountDialog = false
                    },
                    onSuccess = {
                        showAddAccountDialog = false
                        showAccountCreatedDialog = true
                        //TODO: implementar creacion de cuenta
                    }
                )
            }

            if (showAccountCreatedDialog) {
                AccountCreatedDialog(
                    onDismiss = { showAccountCreatedDialog = false }
                )
            }

            if (showLoginDialog) {
                LoginDialog(
                    onDismiss = { showLoginDialog = false },
                    onSuccess = { account ->
                        // si la vista regresa success cambiamos valores de cuenta actuales
                        currentAccount = account
                        loggedIn = true
                        isPremium = account.isPremium
                        showLoginDialog = false

                         // Cargar las tareas de la cuenta
                        tasksList.clear()
                        tasksList.addAll(AccountStorage.getTasksForAccount(context, account.id))
                    }
                )
            }

            if (showUserManualDialog) {
                UserManualDialog(
                    onDismiss = { showUserManualDialog = false }
                )
            }

            if (showSettingsDialog) {
                SettingsDialog(
                    currentAccount = currentAccount,
                    onDismiss = { showSettingsDialog = false },
                    onAccountRefresh = { updated ->
                        currentAccount = updated
                        loggedIn = true
                    }
                )
            }

            if (showLoginRequiredForTask) {
                AlertDialog(
                    onDismissRequest = { showLoginRequiredForTask = false },
                    title = { Text("Sesión requerida", color = MaterialTheme.colorScheme.onBackground) },
                    text = { 
                        Text(
                            "Debes iniciar sesión para poder crear tareas.", 
                            color = MaterialTheme.colorScheme.onBackground
                        ) 
                    },
                    confirmButton = {
                        Button(
                            onClick = { 
                                showLoginRequiredForTask = false 
                                showLoginDialog = true
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            )
                        ) {
                            Text("Iniciar sesión")
                        }
                    },
                    dismissButton = {
                        Button(
                            onClick = { showLoginRequiredForTask = false },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.secondary,
                                contentColor = MaterialTheme.colorScheme.onSecondary
                            )
                        ) {
                            Text("Cancelar")
                        }
                    },
                    containerColor = MaterialTheme.colorScheme.surface
                )
            }
        }
    }
    if (showRewards) {
        RewardsScreen(
            context = context,
            currentAccount = currentAccount,
            onBack = { showRewards = false },
            onAccountRefresh = { updatedAccount ->
                currentAccount = updatedAccount
                loggedIn = updatedAccount != null
                isPremium = updatedAccount?.isPremium ?: false
            }
        )
    }
    if (showLoginForStore) {
        AlertDialog(
            onDismissRequest = { showLoginForStore = false },
            title = { Text("Inicia sesión para acceder a la tienda", color = MaterialTheme.colorScheme.onSurface) },
            text = { Text("Debes iniciar sesión para ver y canjear recompensas.", color = MaterialTheme.colorScheme.onSurface) },
            confirmButton = {
                Button(onClick = {
                    showLoginForStore = false
                    showLoginDialog = true
                }) {
                    Text("Iniciar sesión")
                }
            },
            dismissButton = {
                Button(onClick = { showLoginForStore = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    taskToEdit?.let { task ->
        EditTaskDialog(
            task = task,
            onDismiss = { taskToEdit = null },
            onSave = { updatedTask ->
                if (currentAccount != null) {
                    AccountStorage.updateTaskOfAccount(context, currentAccount!!.id, updatedTask)
                    val updatedTasks = AccountStorage.getTasksForAccount(context, currentAccount!!.id)
                    tasksList.clear()
                    tasksList.addAll(updatedTasks)
                }
                taskToEdit = null
            }
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun RewardsScreen(
    context: Context,
    currentAccount: Account? = null,
    onBack: (() -> Unit)? = null,
    onAccountRefresh: ((Account) -> Unit)? = null
) {
    data class Reward(val id: Int, val title: String, val cost: Int,val themeId:Int)

    val rewards = listOf(
        Reward(1, "Tema Rosa", 100,5),
        Reward(2, "Tema Oceano", 100,6),
        Reward(3, "Tema Verde", 100,7),
        Reward(4, "Tema Amarillo", 100,8),
        Reward(4, "Tema Rojo", 100,9),
        Reward(5, "Tema Morado", 100,10),
        Reward(6, "Tema Naranja", 100,11),
        Reward(7, "Tema Panda", 100,12),
        Reward(8, "Tema Arcoiris", 100, 13) ,
        Reward(4, "Tema Volcan", 100,14),
        Reward(4, "Tema Galaxia", 100,15),
        Reward(5, "Tema Vampiro", 100,16),
        Reward(6, "Tema Pastel", 100,17),
        Reward(7, "Tema Sandia", 100,18)

    )

    val purchasedIds = remember { mutableStateSetOf<Int>() }
    LaunchedEffect(currentAccount?.id) {
        purchasedIds.clear()
        currentAccount?.purchasedRewards?.forEach { purchasedIds.add(it) }
    }

    var processingId by remember { mutableStateOf<Int?>(null) }
    var showBuyConfirmation by remember { mutableStateOf<Reward?>(null) }
    var showNotEnoughPoints by remember { mutableStateOf<Reward?>(null) }
    val points = currentAccount?.points ?: 0
    val themeState = LocalThemeId.current


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {  }
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {

        val surfaceHeight = 800.dp

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(surfaceHeight)
                .clip(RoundedCornerShape(16.dp)),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 4.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            ) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Recompensas",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = "Puntos: $points",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))
                val gridState = rememberLazyGridState()


                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    state = gridState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(8.dp)
                ) {
                    items(rewards) { r ->
                        val isBought = purchasedIds.contains(r.themeId) || (currentAccount?.purchasedRewards?.contains(r.themeId) ?: false)

                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp)),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            tonalElevation = 2.dp
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(12.dp)
                            ) {
                                Spacer(modifier = Modifier.height(8.dp))

                                ThemeDiagonalCirclePreview(
                                    themeId = r.themeId,
                                    modifier = Modifier.size(56.dp)
                                )

                                Text(r.title, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Spacer(Modifier.height(6.dp))
                                Text("${r.cost} pts", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Spacer(Modifier.height(8.dp))

                                if (isBought) {
                                    Text(
                                        "Comprado",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier
                                            .height(36.dp)
                                            .width(120.dp)
                                            .wrapContentHeight(align = Alignment.CenterVertically)
                                    )
                                } else {
                                    Button(
                                        onClick = {
                                            val acc = currentAccount
                                            if (acc != null && acc.points >= r.cost) showBuyConfirmation = r
                                            else showNotEnoughPoints = r
                                        },
                                        modifier = Modifier
                                            .height(36.dp)
                                            .width(120.dp),
                                        enabled = processingId == null
                                    ) {
                                        Text("Comprar", style = MaterialTheme.typography.bodySmall)
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                    Button(onClick = { onBack?.invoke() }) {
                        Text("Regresar")
                    }
                }

                if (showBuyConfirmation != null && currentAccount != null) {
                    val reward = showBuyConfirmation!!
                    AlertDialog(
                        onDismissRequest = { showBuyConfirmation = null },
                        title = { Text("Confirmar canje", color = MaterialTheme.colorScheme.onSurface) },
                        text = { Text("¿Deseas canjear '${reward.title}' por ${reward.cost} puntos?", color = MaterialTheme.colorScheme.onSurface) },
                        confirmButton = {
                            Button(onClick = {
                                val acc = currentAccount ?: return@Button
                                processingId = reward.id
                                val updated = AccountStorage.purchaseReward(context, acc.id, reward.themeId, reward.cost)
                                if (updated == null) {
                                    Toast.makeText(context, "No se pudo completar la compra", Toast.LENGTH_SHORT).show()
                                } else {
                                    onAccountRefresh?.invoke(updated)
                                    purchasedIds.add(reward.themeId)
                                    Toast.makeText(context, "Recompensa canjeada", Toast.LENGTH_SHORT).show()
                                }
                                processingId = null
                                showBuyConfirmation = null
                            }) { Text("Confirmar") }
                        },
                        dismissButton = {
                            Button(onClick = {
                                processingId = null
                                showBuyConfirmation = null
                            }) { Text("Cancelar") }
                        },
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                }

                if (showNotEnoughPoints != null) {
                    val r = showNotEnoughPoints!!
                    AlertDialog(
                        onDismissRequest = { showNotEnoughPoints = null },
                        title = { Text("Puntos insuficientes", color = MaterialTheme.colorScheme.onSurface) },
                        text = { Text("No tienes suficientes puntos para canjear '${r.title}'.", color = MaterialTheme.colorScheme.onSurface) },
                        confirmButton = {
                            Button(onClick = { showNotEnoughPoints = null }) { Text("Aceptar") }
                        },
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                }
            }
        }
    }
}

@Composable
fun ThemeDiagonalCirclePreview(
    themeId: Int,
    modifier: Modifier = Modifier,
    diameterDp: Dp = 56.dp,
    stripeAngleDegrees: Float = 45f,
    fallbackColors: List<Color> = listOf(Color.LightGray, Color.Gray, Color.DarkGray)
) {
    val colors = (rewardThemeColors[themeId] ?: fallbackColors).distinct()
    val strokeColor = MaterialTheme.colorScheme.onSurfaceVariant

    Canvas(modifier = modifier.size(diameterDp)) {
        val w = size.width
        val h = size.height
        val center = Offset(w / 2f, h / 2f)

        val circlePath = Path().apply {
            addOval(Rect(0f, 0f, w, h))
        }

        val stripeCount = colors.size.coerceAtLeast(1)
        val stripeWidth = (w * 1f) / stripeCount
        val startX = 0f

        clipPath(circlePath) {
            rotate(degrees = stripeAngleDegrees, pivot = center) {
                var x = startX
                for (color in colors) {
                    drawRect(
                        color = color,
                        topLeft = Offset(x, -h),
                        size = Size(stripeWidth + 1f, h * 3f)
                    )
                    x += stripeWidth
                }
            }
        }
        val strokeWidthPx = 4.dp.toPx()
        drawCircle(
            color = strokeColor,
            radius = (w / 2f) - strokeWidthPx / 2f, // ajustar para que no se corte
            center = center,
            style = Stroke(width = strokeWidthPx)
        )
    }
}

@Composable
fun commonOutlinedColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = MaterialTheme.colorScheme.onBackground,
    unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
    cursorColor = MaterialTheme.colorScheme.onBackground,
    focusedBorderColor = MaterialTheme.colorScheme.onBackground,
    unfocusedBorderColor = MaterialTheme.colorScheme.onBackground,
    disabledTextColor = MaterialTheme.colorScheme.onBackground
)

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskDialog(
    onDismiss: () -> Unit,
    onAddTask: (Task) -> Unit,
    nextId: Int
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            tonalElevation = 4.dp,
            color = MaterialTheme.colorScheme.background,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.9f) // casi pantalla completa
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("Nueva Tarea", style = MaterialTheme.typography.titleLarge)

                var taskName by remember { mutableStateOf("") }
                var taskDescription by remember { mutableStateOf("") }
                var taskDueDate by remember { mutableStateOf(LocalDate.now()) }
                var taskDueTime by remember { mutableStateOf(LocalTime.now()) }
                var selectedDifficulty by remember { mutableStateOf(1) }
                var expanded by remember { mutableStateOf(false) }
                var taskSteps = remember { mutableStateListOf<String>() }

                val context = LocalContext.current

                OutlinedTextField(
                    value = taskName,
                    onValueChange = { taskName = it },
                    label = { Text("Nombre de la tarea", color = MaterialTheme.colorScheme.onBackground) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = commonOutlinedColors()
                )

                OutlinedTextField(
                    value = taskDescription,
                    onValueChange = { taskDescription = it },
                    label = { Text("Descripcion", color = MaterialTheme.colorScheme.onBackground) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = commonOutlinedColors()
                )

                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = taskDueDate.format(dateFormatter),
                        onValueChange = {},
                        label = { Text("Fecha de entrega" ,color = MaterialTheme.colorScheme.onBackground)},
                        modifier = Modifier.fillMaxWidth(),
                        readOnly = true,
                        textStyle = LocalTextStyle.current.copy(color = MaterialTheme.colorScheme.onBackground),
                        colors = commonOutlinedColors()
                    )

                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .clickable {
                                val picker = DatePickerDialog(
                                    context,
                                    { _, year, month, dayOfMonth ->
                                        taskDueDate = LocalDate.of(year, month + 1, dayOfMonth)
                                    },
                                    taskDueDate.year,
                                    taskDueDate.monthValue - 1,
                                    taskDueDate.dayOfMonth
                                )
                                picker.show()
                            },
                    )
                }

                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = taskDueTime.format(timeFormatter),
                        onValueChange = {},
                        label = { Text("Hora de entrega", color = MaterialTheme.colorScheme.onBackground) },
                        modifier = Modifier.fillMaxWidth(),
                        readOnly = true,
                        textStyle = LocalTextStyle.current.copy(color = MaterialTheme.colorScheme.onBackground),
                        colors = commonOutlinedColors()
                    )

                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .clickable {
                                val picker = TimePickerDialog(
                                    context,
                                    { _, hour, minute ->
                                        taskDueTime = LocalTime.of(hour, minute)
                                    },
                                    taskDueTime.hour,
                                    taskDueTime.minute,
                                    true
                                )
                                picker.show()
                            }
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Dificultad:", color = MaterialTheme.colorScheme.onBackground)

                    Box {
                        Button(onClick = { expanded = true }) {
                            Text(selectedDifficulty.toString())
                        }
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            (1..5).forEach { level ->
                                DropdownMenuItem(
                                    text = { Text(level.toString()) },
                                    onClick = {
                                        selectedDifficulty = level
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Pasos", color = MaterialTheme.colorScheme.onBackground)

                    taskSteps.forEachIndexed { index, step ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OutlinedTextField(
                                value = step,
                                onValueChange = { newValue ->
                                    taskSteps[index] = newValue
                                },
                                label = { Text("Paso ${index + 1}", color = MaterialTheme.colorScheme.onBackground) },
                                modifier = Modifier.weight(1f),
                                colors = commonOutlinedColors()
                            )

                            IconButton(onClick = { taskSteps.removeAt(index) }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Eliminar paso"
                                )
                            }
                        }
                    }

                    Button(
                        onClick = { taskSteps.add("") },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("Agregar paso")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)
                ) {
                    Button(onClick = onDismiss) {
                        Text("Regresar")
                    }
                    Button(
                        onClick = {
                            val newTask = Task(
                                title = if (taskName == "") "Tarea sin nombre" else taskName,
                                description = if (taskDescription == "") "Tarea sin descripcion" else taskDescription,
                                dueDate = taskDueDate,
                                dueTime = taskDueTime.withSecond(0).withNano(0),
                                difficulty = selectedDifficulty,
                                steps = convertStringsToSteps(taskSteps),
                                id = nextId
                            )
                            onAddTask(newTask)
                        }
                    ) {
                        Text("Agregar tarea")
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTaskDialog(
    task: Task,
    onDismiss: () -> Unit,
    onSave: (Task) -> Unit
) {

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            tonalElevation = 4.dp,
            color = MaterialTheme.colorScheme.background,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.9f)
                .padding(16.dp)
        ) {

            var taskName by remember { mutableStateOf(task.title) }
            var taskDescription by remember { mutableStateOf(task.description) }
            var taskDueDate by remember { mutableStateOf(task.dueDate) }
            var taskDueTime by remember { mutableStateOf(task.dueTime) }
            var selectedDifficulty by remember { mutableStateOf(task.difficulty) }
            var expanded by remember { mutableStateOf(false) }
            var taskSteps = remember { mutableStateListOf<Step>().apply {
                addAll(task.steps.map { it.copy() })
            } }

            val context = LocalContext.current


            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                Text("Editar Tarea", style = MaterialTheme.typography.titleLarge)

                OutlinedTextField(
                    value = taskName,
                    onValueChange = { taskName = it },
                    label = { Text("Nombre de la tarea", color = MaterialTheme.colorScheme.onBackground) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = commonOutlinedColors()
                )

                OutlinedTextField(
                    value = taskDescription,
                    onValueChange = { taskDescription = it },
                    label = { Text("Descripción", color = MaterialTheme.colorScheme.onBackground) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = commonOutlinedColors()
                )

                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = taskDueDate.format(dateFormatter),
                        onValueChange = {},
                        label = { Text("Fecha de entrega", color = MaterialTheme.colorScheme.onBackground) },
                        modifier = Modifier.fillMaxWidth(),
                        readOnly = true,
                        colors = commonOutlinedColors()
                    )

                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .clickable {
                                DatePickerDialog(
                                    context,
                                    { _, year, month, dayOfMonth ->
                                        taskDueDate = LocalDate.of(year, month + 1, dayOfMonth)
                                    },
                                    taskDueDate.year,
                                    taskDueDate.monthValue - 1,
                                    taskDueDate.dayOfMonth
                                ).show()
                            }
                    )
                }


                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = taskDueTime.format(timeFormatter),
                        onValueChange = {},
                        label = { Text("Hora de entrega", color = MaterialTheme.colorScheme.onBackground) },
                        modifier = Modifier.fillMaxWidth(),
                        readOnly = true,
                        colors = commonOutlinedColors()
                    )

                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .clickable {
                                TimePickerDialog(
                                    context,
                                    { _, hour, minute ->
                                        taskDueTime = LocalTime.of(hour, minute)
                                    },
                                    taskDueTime.hour,
                                    taskDueTime.minute,
                                    true
                                ).show()
                            }
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Dificultad:")

                    Box {
                        Button(onClick = { expanded = true }) {
                            Text(selectedDifficulty.toString())
                        }
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            (1..5).forEach { level ->
                                DropdownMenuItem(
                                    text = { Text(level.toString()) },
                                    onClick = {
                                        selectedDifficulty = level
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                Text("Pasos")

                taskSteps.forEachIndexed { index, step ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = step.text,
                            onValueChange = { newText ->
                                taskSteps[index] = step.copy(text = newText) },
                            modifier = Modifier.weight(1f),
                            label = { Text("Paso ${index + 1}",color = MaterialTheme.colorScheme.onBackground) },

                        )

                        IconButton(onClick = { taskSteps.removeAt(index) }) {
                            Icon(Icons.Default.Close, contentDescription = null)
                        }
                    }
                }

                Button(onClick = { taskSteps.add(Step("") )}) {
                    Text("Agregar paso",)
                }

                Spacer(modifier = Modifier.height(10.dp))


                Row(
                    modifier = Modifier.fillMaxWidth(),horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.End))

                {
                    Button(onClick = onDismiss) {
                        Text("Cancelar")
                    }

                    Button(onClick = { onSave(task.copy(
                                title = taskName,
                                description = taskDescription,
                                dueDate = taskDueDate,
                                dueTime = taskDueTime,
                                difficulty = selectedDifficulty,
                                steps = taskSteps.toMutableList()
                            )
                        )
                    },
                        modifier = Modifier.wrapContentWidth()

                    ) {
                        Text("Guardar cambios",fontSize = 12.sp, maxLines = 1)
                    }
                }
            }
        }
    }
}

// Funcion para crear una cuenta
@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAccountDialog(
    onDismiss: () -> Unit,
    onSuccess: () -> Unit,
    //onAddAccount: (Account) -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            tonalElevation = 4.dp,
            color = MaterialTheme.colorScheme.background,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.9f) // casi pantalla completa
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("Crear Cuenta", style = MaterialTheme.typography.titleLarge)

                var accountName by remember { mutableStateOf("") }
                var accountEmail by remember { mutableStateOf("") }
                var accountPassword by remember { mutableStateOf("") }
                var accountConfirmPassword by remember { mutableStateOf("") }
                var accountPremium by remember { mutableStateOf(false) }

                var emailErrorEmpty by remember { mutableStateOf(false) }
                var passwordErrorEmpty by remember { mutableStateOf(false) }
                var confirmPasswordErrorMismatch by remember { mutableStateOf(false) }
                var emailErrorUsed by remember { mutableStateOf(false) }
                var emailErrorInvalid by remember { mutableStateOf(false) }

                OutlinedTextField(
                    value = accountName,
                    onValueChange = { accountName = it },
                    label = { Text("Nombre de la cuenta", color = MaterialTheme.colorScheme.onBackground) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = commonOutlinedColors()
                )

                 OutlinedTextField(
                    value = accountEmail,
                    onValueChange = { 
                        accountEmail = it.replace("\\s".toRegex(), "") // eliminando espacios del email
                    },
                    label = { Text("Email", color = MaterialTheme.colorScheme.onBackground) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = commonOutlinedColors()
                )
                if (emailErrorEmpty) {
                    Text(
                        text = "El correo no puede estar vacío",
                        color = MaterialTheme.colorScheme.onBackground,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                if (emailErrorUsed) {
                    Text(
                        text = "El correo ya está en uso",
                        color = MaterialTheme.colorScheme.onBackground,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                if (emailErrorInvalid) {
                    Text(
                        text = "El correo no es válido",
                        color = MaterialTheme.colorScheme.onBackground,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                OutlinedTextField(
                    value = accountPassword,
                    onValueChange = { accountPassword = it },
                    label = { Text("Contraseña", color = MaterialTheme.colorScheme.onBackground) },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = PasswordVisualTransformation(), // Ocultando contraseña
                    colors = commonOutlinedColors()
                )
                if (passwordErrorEmpty) {
                    Text(
                        text = "La contraseña no puede estar vacía",
                        color = MaterialTheme.colorScheme.onBackground,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                OutlinedTextField(
                    value = accountConfirmPassword,
                    onValueChange = { accountConfirmPassword = it },
                    label = { Text("Confirmar contraseña", color = MaterialTheme.colorScheme.onBackground) },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = PasswordVisualTransformation(), // Ocultando contraseña
                    colors = commonOutlinedColors()
                )
                if (confirmPasswordErrorMismatch) {
                    Text(
                        text = "Las contraseñas no coinciden",
                        color = MaterialTheme.colorScheme.onBackground,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Checkbox(
                        checked = accountPremium,
                        onCheckedChange = { accountPremium = it },
                        colors = CheckboxDefaults.colors(
                            checkedColor = MaterialTheme.colorScheme.primary,
                            uncheckedColor = MaterialTheme.colorScheme.onBackground,
                            checkmarkColor = MaterialTheme.colorScheme.onPrimary
                        )
                    )
                    Text("Premium", color = MaterialTheme.colorScheme.onBackground)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)
                ) {
                    Button(onClick = onDismiss) {
                        Text("Regresar")
                    }
                    val context = LocalContext.current
                    Button(
                        onClick = {
                            // Resetear errores
                            emailErrorEmpty = false
                            passwordErrorEmpty = false
                            confirmPasswordErrorMismatch = false
                            emailErrorUsed = false
                            emailErrorInvalid = false

                            // Validaciones
                            if (accountEmail.isEmpty()) {
                                emailErrorEmpty = true
                                return@Button
                            }
                            if (accountPassword.isEmpty()) {
                                passwordErrorEmpty = true
                                return@Button
                            }
                            if (accountPassword != accountConfirmPassword) {
                                confirmPasswordErrorMismatch = true
                                return@Button
                            }
                            if (!Patterns.EMAIL_ADDRESS.matcher(accountEmail).matches()) {
                                emailErrorInvalid = true
                                return@Button
                            }
                            // Revisar si correo en uso
                            if (AccountStorage.isEmailUsed(context, accountEmail)) {
                                emailErrorUsed = true
                                return@Button
                            }

                            // Creación de cuenta
                            val newAccount = Account(
                                id = AccountStorage.getNextId(context),
                                name = accountName,
                                email = accountEmail,
                                password = accountPassword,
                                isPremium = accountPremium,
                                points = 0,
                                createdAt = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                            )
                            
                            // Guardando en el json
                            AccountStorage.addAccount(context, newAccount)
                            
                            onSuccess()
                        }
                    ) {
                        Text("Crear cuenta")
                    }
                }
            }
        }
    }
}

// Función para loguear en una cuenta
@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginDialog(
    onDismiss: () -> Unit,
    onSuccess: (Account) -> Unit, // pasamos la cuenta logueada
) {
    val context = LocalContext.current
    
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            tonalElevation = 4.dp,
            color = MaterialTheme.colorScheme.background,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.6f) // Más pequeño que el de registro
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("Iniciar Sesión", style = MaterialTheme.typography.titleLarge)

                var accountEmail by remember { mutableStateOf("") }
                var accountPassword by remember { mutableStateOf("") }

                var emailError by remember { mutableStateOf(false) }
                var passwordError by remember { mutableStateOf(false) }
                var loginError by remember { mutableStateOf(false) }

                // Campo de email
                OutlinedTextField(
                    value = accountEmail,
                    onValueChange = { 
                        accountEmail = it.replace("\\s".toRegex(), "") // eliminando espacios del email
                    },
                    label = { Text("Email", color = MaterialTheme.colorScheme.onBackground) },
                    modifier = Modifier.fillMaxWidth(),
                    isError = emailError || loginError
                )
                if (emailError) {
                    Text(
                        text = "El correo no puede estar vacío",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                // Campo de contraseña
                OutlinedTextField(
                    value = accountPassword,
                    onValueChange = { accountPassword = it },
                    label = { Text("Contraseña", color = MaterialTheme.colorScheme.onBackground) },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = PasswordVisualTransformation(), // ocultando contraseña
                    isError = passwordError || loginError
                )
                if (passwordError) {
                    Text(
                        text = "La contraseña no puede estar vacía",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                if (loginError) {
                    Text(
                        text = "Correo o contraseña incorrectos",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)
                ) {
                    Button(onClick = onDismiss) {
                        Text("Regresar")
                    }
                    Button(
                        onClick = {
                            // Resetear errores
                            emailError = false
                            passwordError = false
                            loginError = false

                            // Validaciones
                            if (accountEmail.isEmpty()) {
                                emailError = true
                                return@Button
                            }
                            if (accountPassword.isEmpty()) {
                                passwordError = true
                                return@Button
                            }

                            // Intentar login
                            val account = AccountStorage.validateLogin(context, accountEmail, accountPassword)
                            if (account != null) {
                                SessionStorage.saveSession(context, account) 
                                onSuccess(account)
                            } else {
                                loginError = true
                            }
                        }
                    ) {
                        Text("Iniciar sesión")
                    }
                }
            }
        }
    }
}

// Funcion para mostrar dialogo de cuenta creada
@Composable
fun AccountCreatedDialog(
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Cuenta creada", color = MaterialTheme.colorScheme.onBackground) },
        text = { Text("Su cuenta ha sido creada exitosamente.", color = MaterialTheme.colorScheme.onBackground) },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text("Aceptar")
            }
        },
        containerColor = MaterialTheme.colorScheme.surface
    )
}

// Funcion para mostrar dialogo de tarea creada
@Composable
fun TaskCreatedDialog(
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Tarea creada", color = MaterialTheme.colorScheme.onBackground) },
        text = { Text("Su tarea ha sido creada exitosamente.", color = MaterialTheme.colorScheme.onBackground) },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text("Aceptar")
            }
        },
        containerColor = MaterialTheme.colorScheme.surface
    )
}

// Funcion para mostrar dialogo de tarea no creada por limite alcanzado, para usuarios no premium
@Composable
fun TaskNotCreatedDialogNonPremium(
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Tarea no creada", color = MaterialTheme.colorScheme.onBackground) },
        text = {
            Text("No se pudo crear/descompletar la tarea. Ha alcanzado el límite de tareas.\nConsidera completar tareas existentes o usar una cuenta Premium para tener más tareas activas.",
            color = MaterialTheme.colorScheme.onBackground)
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text("Aceptar")
            }
        },
        containerColor = MaterialTheme.colorScheme.surface
    )
}

// Funcion para mostrar dialogo de tarea no creada por limite alcanzado, para usuarios  premium
@Composable
fun TaskNotCreatedDialogPremium(
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Tarea no creada", color = MaterialTheme.colorScheme.onBackground) },
        text = {
            Text("No se pudo crear/descompletar la tarea. Ha alcanzado el límite de tareas.\nConsidera completar tareas existentes.",
            color = MaterialTheme.colorScheme.onBackground)
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text("Aceptar")
            }
        },
        containerColor = MaterialTheme.colorScheme.surface
    )
}

// Funcion para mostrar dialogo de tarea completada
@Composable
fun TaskCompletedDialog(
    pointsEarned: Int,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Tarea completada", color = MaterialTheme.colorScheme.onBackground) },
        text = {
            if (pointsEarned > 0) {
                Text("¡Felicidades! Has completado la tarea y ganado $pointsEarned puntos.", color = MaterialTheme.colorScheme.onBackground)
            } else {
                Text("¡Felicidades! Has completado la tarea.", color = MaterialTheme.colorScheme.onBackground)
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text("Aceptar")
            }
        },
        containerColor = MaterialTheme.colorScheme.surface
    )
}


// Funcion para mostrar el manual de usuario o tutorial
@Composable
fun UserManualDialog(
    onDismiss: () -> Unit,
) {
    var currentPage by remember { mutableStateOf(0) }
    val totalPages = 8
    // Titulo y descripcion de cada pagina
    val pageContents = listOf(
        Pair("Bienvenido a FocusUp", "¡Tu asistente personal para mantenerte enfocado y organizado!"),
        Pair("Cuentas", "Registra e inicia sesion en una cuenta para poder usar las funciones de la app."),
        Pair("Agregar Tareas", "Crea tareas con detalles como fecha, hora, dificultad y pasos a seguir."),
        Pair("Visualizar Tareas", "Consulta tus tareas en un calendario mensual."),
        Pair("Notificaciones", "Recibe recordatorios para tus tareas importantes."),
        Pair("Gana Recompensas", "Termina tareas, gana puntos y compra temas en la tienda."),
        Pair("Configuracion", "Modifica la hora de notificaciones y cambia los temas de la app."),
        Pair("Cuenta Premium", "Desbloquea funciones exclusivas y mejora tu experiencia."),
    )
    // Imagenes de cada pagina, van en app/src/main/res/drawable/
    val pageImages = listOf(
        R.drawable.logo,
        R.drawable.manual_cuenta,
        R.drawable.manual_agregar_tarea,
        R.drawable.manual_visualizar_tarea,
        R.drawable.manual_recordatorio,
        R.drawable.manual_tienda,
        R.drawable.manual_configuracion,
        R.drawable.manual_premium
    )
    // Contenido de la pagina actual
    val currentContent = pageContents[currentPage]
    val currentImage = pageImages[currentPage]
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(currentContent.first, color = MaterialTheme.colorScheme.onBackground) },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = currentImage),
                    contentDescription = null,
                    modifier = Modifier
                        .size(300.dp)
                        .clip(RoundedCornerShape(16.dp))
                )
                Text(
                    currentContent.second,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center
                )
            }
        },
        confirmButton = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Boton anterior
                if (currentPage > 0) {
                    Button(
                        onClick = { currentPage-- },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Text("Anterior")
                    }
                } else {
                    Spacer(modifier = Modifier.width(8.dp))
                }
                // Boton siguiente o cerrar
                Button(
                    onClick = {
                        if (currentPage < totalPages - 1) currentPage++ else onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text(if (currentPage < totalPages - 1) "Siguiente" else "Cerrar")
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.surface
    )
}

// Funcion para mostrar el dialogo de configuraciones
@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsDialog(
    currentAccount: Account? = null,
    onDismiss: () -> Unit,
    onAccountRefresh: ((Account) -> Unit)? = null
) {
    val context = LocalContext.current
    val themeState = LocalThemeId.current
    var hour by remember { mutableStateOf(AppSettings.getReminderHour(context)) }
    var minute by remember { mutableStateOf(AppSettings.getReminderMinute(context)) }

    val builtInThemeIds = listOf(
        ThemeIds.LIGHT1,
        ThemeIds.DARK1,
        ThemeIds.LIGHT2,
        ThemeIds.DARK2
    )

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            tonalElevation = 4.dp,
            color = MaterialTheme.colorScheme.background,
            modifier = Modifier
        ) {
            Column(
                modifier = Modifier
                    .height(520.dp)
                    .padding(16.dp)
            ) {
                Text(
                    "Configuraciones",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(8.dp))

                Column {
                    Text(
                        "Hora de notificación actual: %02d:%02d".format(hour, minute),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {
                            TimePickerDialog(
                                context,
                                { _, selectedHour: Int, selectedMinute: Int ->
                                    AppSettings.saveReminderTime(context, selectedHour, selectedMinute)
                                    scheduleDailyReminder(context)
                                    hour = selectedHour
                                    minute = selectedMinute
                                },
                                hour,
                                minute,
                                true
                            ).show()
                        }
                    ) {
                        Text("Modificar hora")
                    }
                }
                val themes = listOf(
                    "Claro 1" to ThemeIds.LIGHT1,
                    "Oscuro 1" to ThemeIds.DARK1,
                    "Claro 2" to ThemeIds.LIGHT2,
                    "Oscuro 2" to ThemeIds.DARK2,
                    "Rosa" to ThemeIds.PINK,
                    "Océano" to ThemeIds.OCEAN,
                    "Verde" to ThemeIds.GREEN,
                    "Amarillo" to ThemeIds.YELLOW,
                    "Rojo" to ThemeIds.RED,
                    "Morado" to ThemeIds.PURPLE,
                    "Naranja" to ThemeIds.ORANGE,
                    "Panda" to ThemeIds.PANDA,
                    "Arcoíris" to ThemeIds.RAINBOW,
                    "Volcan" to ThemeIds.VOLCANO,
                    "Galaxia" to ThemeIds.GALAXY,
                    "Vampiro" to ThemeIds.VAMPIRE,
                    "Pastel" to ThemeIds.CAKE,
                    "Sandia" to ThemeIds.WATERMELON,
                    )



                val gridState = rememberLazyGridState()
                val purchasedIds = remember(currentAccount) {
                    currentAccount?.purchasedRewards?.toSet() ?: emptySet()
                }
                Spacer(modifier = Modifier.height(8.dp))

                Text("Tema de la app", color = MaterialTheme.colorScheme.onBackground)

                Spacer(modifier = Modifier.height(4.dp))

                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    state = gridState,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(horizontal = 4.dp, vertical = 6.dp)
                ) {items(themes) { (label, id) ->
                    val isLocked = if (currentAccount == null) {
                        !builtInThemeIds.contains(id)
                    } else {
                        !(builtInThemeIds.contains(id) || purchasedIds.contains(id))
                    }
                    ThemeItem(
                        themeId = id,
                        title = label,
                        isLocked = isLocked,
                        modifier = Modifier
                            .height(90.dp)
                            .padding(4.dp),

                        onClick = { selectedId ->
                            if (!isLocked) {
                                if (currentAccount != null) {
                                    currentAccount.selectedThemeId = selectedId
                                    val ok = AccountStorage.updateAccount(
                                        context,
                                        currentAccount
                                    )
                                    if (ok) {
                                        themeState.value = selectedId
                                        onAccountRefresh?.invoke(currentAccount)
                                        Toast.makeText(
                                            context,
                                            "Tema aplicado",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    } else {
                                        Toast.makeText(
                                            context,
                                            "Error al guardar el tema",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                } else {
                                    AppSettings.saveTheme(context, selectedId)
                                    themeState.value = selectedId
                                    Toast.makeText(
                                        context,
                                        "Tema aplicado",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }

                            }
                        }
                    )
                }
                }
                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(top = 10.dp)
                ) {
                    Text("Cerrar")
                }
            }
        }
    }
}


@Composable
fun ThemeItem(
    themeId: Int,
    title: String,
    isLocked: Boolean,
    onClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(26.dp)
                .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                .background(MaterialTheme.colorScheme.primary), contentAlignment = Alignment.Center
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f, fill = false)
                .clip(RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .clickable(enabled = !isLocked) { onClick(themeId) }
                    .fillMaxWidth()
                    .padding(vertical = 4.dp, horizontal = 4.dp)
                    .wrapContentHeight()
            ) {
                ThemeDiagonalCirclePreview(
                    themeId = themeId,
                    modifier = Modifier.size(52.dp)
                )
            }

            if (isLocked) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(Color.Black.copy(alpha = 0.32f))
                        .clip(
                            RoundedCornerShape(
                                bottomStart = 12.dp,
                                bottomEnd = 12.dp
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Bloqueado",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }
    }
}

// Funcion de preview
@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true, widthDp = 900, heightDp = 480)
@Composable
fun CalendarPreview() {
    FocusUpTheme {
        // Para el preview, pasamos un contexto nulo (no se guardarán datos reales)
        CalendarScreen(context = LocalContext.current)
    }
}

// Funcion para obtener un color segun la dificultad y si la tarea esta completada
@Composable
fun GetTaskColor(difficulty: Int, isCompleted: Boolean): Color {
    if (isCompleted) {
        return SpecialGray
    }
    return when (difficulty) {
        5 -> SpecialRed
        4 -> SpecialOrange
        3 -> SpecialYellow
        2 -> SpecialGreen
        1 -> SpecialBlue
        else -> MaterialTheme.colorScheme.secondary
    }
}

// Funcion para obtener un color para texto segun la dificultad y si la tarea esta completada
@Composable
fun GetTaskColorText(difficulty: Int, isCompleted: Boolean): Color {
    if (isCompleted) {
        return SpecialGray2
    }
    return when (difficulty) {
        5 -> SpecialRed2
        4 -> SpecialOrange2
        3 -> SpecialYellow2
        2 -> SpecialGreen2
        1 -> SpecialBlue2
        else -> MaterialTheme.colorScheme.secondary
    }
}

// Funcion para calcular puntos al completar una tarea
@RequiresApi(Build.VERSION_CODES.O)
fun CalculatePointsForTask(task: Task): Int {
    // Obtenemos la fecha actual
    val now = LocalDateTime.now()
    // Obtenemos la fecha de entrega
    val taskDateTime = LocalDateTime.of(task.dueDate, task.dueTime)
    // Obtenemos la dificultad
    val difficulty = task.difficulty
    // Obtenemos los puntos base por dificultad
    val basePoints = when (difficulty) {
        1 -> 10
        2 -> 20
        3 -> 30
        4 -> 40
        5 -> 50
        else -> 0
    }
    // Obtenemos cuantos dias de anticipacion se completo la tarea
    val daysEarly = Duration.between(now, taskDateTime).toDays().toInt()
    // Limitamos los dias de anticipacion entre 0 y 7
    val limitedDaysEarly = daysEarly.coerceIn(0, 7)
    // Regresamos los puntos finales
    return basePoints + (limitedDaysEarly * difficulty)
}

// Funcion para volver una lista de strings a una lista de steps
fun convertStringsToSteps(stepStrings: List<String>): MutableList<Step> {
    return stepStrings.map { Step(it, false) }.toMutableList()
}

// Funcion para obtener el estatus de la tarea
fun getStatus(isCompleted: Boolean): String {
    return if (isCompleted) " (Completada)" else ""
}

// Funcion para obtener el texto de "Completar tarea" o "Descompletar tarea"
fun getCompleteButtonText(isCompleted: Boolean): String {
    return if (isCompleted) "Descompletar tarea" else "Completar tarea"
}

// Funcion para obtener el texto de "Editar" o "Eliminar"
fun getEditButtonText(isCompleted: Boolean): String {
    return if (isCompleted) "Eliminar" else "Editar"
}