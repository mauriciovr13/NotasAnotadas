package com.example.notasanotadas.notification

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.notasanotadas.R
import com.example.notasanotadas.model.database.AppDatabase
import com.example.notasanotadas.model.database.Note
import com.example.notasanotadas.ui.SplashActivity
import java.util.*

class Alarm : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {

        val noteId = intent?.getIntExtra("noteId", -1)
        val db = AppDatabase.getInstance(context!!)
        val nota = db.noteDAO().getNoteById(noteId!!)

        // Notificação
        createNotificationChannel(context)
        notify(context, nota)

        // Atualiza o próximo dia/hora para notificar
        val calendar : Calendar = Calendar.getInstance().apply {
            set(
                nota.dataHoraAlerta.year,
                nota.dataHoraAlerta.month,
                nota.dataHoraAlerta.day,
                nota.dataHoraAlerta.hour,
                nota.dataHoraAlerta.minute,
                0
            )
        }
        when(nota.periodicidade) {
            1 -> nota.periodicidade = 0 // Uma única vez
            2 -> {
                // Uma vez por hora
                calendar.add(Calendar.HOUR_OF_DAY, 1)
                nota.dataHoraAlerta.hour = calendar.get(Calendar.HOUR_OF_DAY)
            }
            3 -> {
                // Diário
                calendar.add(Calendar.DAY_OF_YEAR, 1)
                nota.dataHoraAlerta.day = calendar.get(Calendar.DAY_OF_MONTH)
            }
            4 -> {
                // Semanal
                calendar.add(Calendar.DAY_OF_YEAR, 7)
                nota.dataHoraAlerta.day = calendar.get(Calendar.DAY_OF_MONTH)
            }
        }

        Log.println(Log.INFO, "Notificacao", "Hora atualizada da próxima notificacao: ${calendar.time}")

        if(nota.periodicidade !== 0) createNotification(context, nota)
        db.noteDAO().update(nota)

    }

    private fun notify(ctx: Context, note : Note) {
        val intent = Intent(ctx, SplashActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(ctx, note.broadcastId.toInt(), intent, 0)

        var builder = NotificationCompat.Builder(ctx, "noteNotify")
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(note?.titulo)
            .setContentText(note?.resumo)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        // Gerar um numero aleatorio e salvar no banco para quando atualizar a nota
        var notificationId : Int = System.currentTimeMillis().toInt()
        Log.println(Log.INFO, "Notificação", "Notificacao enviada")

        var notificationManager = ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(notificationId, builder.build())
    }

    private fun createNotificationChannel(ctx: Context) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Note reminder"
            val descriptionText = "Note reminder"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("noteNotify", name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(ctx : Context, note : Note) {
        val broadcastIntent = Intent(ctx, Alarm::class.java)
        broadcastIntent.putExtra("noteId", note.id)
        val pendingIntent = PendingIntent.getBroadcast(
            ctx,
            note.broadcastId.toInt(),
            broadcastIntent,
            0
        )
        // Calculando os ms para a notificação
        var millisecond = note.dataHoraAlerta.toDate().time
        Log.println(Log.INFO, "Notificação", "Nova notificação criada para $millisecond")
        val alarm = ctx.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarm.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, millisecond, pendingIntent)
    }
}