package com.focusup.focusupapp

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import java.time.LocalDate
import com.focusup.focusupapp.model.Task
import com.focusup.focusupapp.storage.AccountStorage
import com.focusup.focusupapp.storage.SessionStorage
import android.graphics.*

class ReminderReceiver : BroadcastReceiver() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(context: Context, intent: Intent?) {
        // Cargar la sesión activa para obtener la cuenta actual
        val currentAccount = SessionStorage.loadSession(context)
        // Cargar tareas de la cuenta activa
        val tasks: List<Task> = if (currentAccount != null) {
            AccountStorage.getTasksForAccount(context, currentAccount.id)
        } else {
            emptyList()
        }

        // Buscar la tarea mas proxima
        val now = LocalDate.now()
        val upcomingTasks = tasks.filter { it.dueDate >= now && !it.isCompleted }
        val nextTask = upcomingTasks.minWithOrNull(compareBy<Task> { it.dueDate })

        // Buscar la tarea mas dificil
        val hardestTask = upcomingTasks.maxWithOrNull(compareBy<Task> { it.difficulty })

        // Texto de la notificacion
        val notificationText = if (nextTask != null && hardestTask != null) {
            "Próxima tarea: ${nextTask.title}\n" +
            "Tarea más difícil: ${hardestTask.title}"
        } else if (nextTask != null) {
            "Próxima tarea: ${nextTask.title}"
        } else if (hardestTask != null) {
            "Tarea más difícil: ${hardestTask.title}"
        } else {
            "No hay tareas pendientes"
        }

        val originalLogo = BitmapFactory.decodeResource(context.resources, R.drawable.logo)
        val circularLogo = getCircularBitmap(originalLogo)

        // Intent para abrir la MainActivity al tocar la notificacion
        val openAppIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        // PendingIntent para la notificacion
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, "TASK_REMINDER_CHANNEL")
            .setSmallIcon(R.drawable.ic_notification)
            .setLargeIcon(circularLogo)
            .setContentTitle("FocusUp - Recordatorio diario")
            .setContentText(notificationText)
            .setStyle(NotificationCompat.BigTextStyle().bigText(notificationText))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        with(NotificationManagerCompat.from(context)) {
            notify(1001, notification)
        }
    }

    // Funcion para crear un bitmap circular
    private fun getCircularBitmap(bitmap: Bitmap): Bitmap {
        val size = minOf(bitmap.width, bitmap.height)
        val output = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)
        val paint = Paint().apply { isAntiAlias = true }
        val rect = Rect(0, 0, size, size)
        val rectF = RectF(rect)
        canvas.drawCircle(size / 2f, size / 2f, size / 2f, paint)
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        canvas.drawBitmap(bitmap, null, rectF, paint)
        return output
    }
}