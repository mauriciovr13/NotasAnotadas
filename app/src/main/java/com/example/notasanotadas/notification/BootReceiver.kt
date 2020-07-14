package com.example.notasanotadas.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.notasanotadas.model.database.AppDatabase
import com.example.notasanotadas.model.database.Note

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == "android.intent.action.BOOT_COMPLETED") {
            val db = AppDatabase.getInstance(context!!)
            val notas = db.noteDAO().getAllNotes()
            notas.forEach {
                createNotification(context, it)
            }
        }
    }

    private fun createNotification(context: Context, note : Note) {
        val broadcastIntent = Intent(context, Alarm::class.java)
        broadcastIntent.putExtra("noteId", note.id)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            note.broadcastId.toInt(),
            broadcastIntent,
            0
        )
        // Calculando os ms para a notificação
        var millisecond = note.dataHoraAlerta.toDate().time
        val alarm = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarm.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, millisecond, pendingIntent)
    }
}