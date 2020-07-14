package com.example.notasanotadas.ui

import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.notasanotadas.R
import com.example.notasanotadas.model.DateTime
import com.example.notasanotadas.model.database.AppDatabase
import com.example.notasanotadas.model.database.Note
import com.example.notasanotadas.notification.Alarm
import kotlinx.android.synthetic.main.nova_nota_activity.*
import java.util.*

class NewNoteActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener,
    TimePickerDialog.OnTimeSetListener {

    var day = 0
    var month: Int = 0
    var year: Int = 0
    var hour: Int = 0
    var minute: Int = 0
    var myDay = 0
    var myMonth: Int = 0
    var myYear: Int = 0
    var myHour: Int = 0
    var myMinute: Int = 0
    lateinit var db : AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.nova_nota_activity)
        setupDatabase()

        val action : String = intent.getStringExtra("action")
        val position : Int = intent.getIntExtra("position", -1)

        if (spinner != null) {
            val periodicityList = resources.getStringArray(R.array.periodicidade)
            val adapter = ArrayAdapter(this,
                R.layout.spinner_item, periodicityList)
            spinner.adapter = adapter
        }
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>,
                                        view: View, position: Int, id: Long) {
                Log.println(Log.INFO, "SPINNER", "Posição selecionado ${position}")
                if(position == 0) txtDateTime.text = ""
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
        }

        if (position !== -1) {
            val nota = db.noteDAO().getNoteById(position)

            txtTitulo.setText(nota.titulo)
            txtResumo.setText(nota.resumo)
            txtDescricao.setText(nota.descricao)
            txtDateTime.text = nota.dataHoraAlerta.toString()
            spinner.setSelection(nota.periodicidade)

            // Setando os valores de data e hora
            myYear = nota.dataHoraAlerta.year
            myMonth = nota.dataHoraAlerta.month
            myDay = nota.dataHoraAlerta.day
            myHour = nota.dataHoraAlerta.hour
            myMinute = nota.dataHoraAlerta.minute

            // Alterando o nome do botao
            btnNote.setText(R.string.edit)
        }

        datePicker.setOnClickListener {
            val calendar: Calendar = Calendar.getInstance()
            day = calendar.get(Calendar.DAY_OF_MONTH)
            month = calendar.get(Calendar.MONTH)
            year = calendar.get(Calendar.YEAR)
            val datePickerDialog = DatePickerDialog(this, this, year, month, day)
            datePickerDialog.show()
        }

        btnNote.setOnClickListener {
            var titulo : String = ""
            var resumo : String = ""
            var descricao : String = ""
            var dateTime : String = ""
            var periodicidade : Int = spinner.selectedItemPosition
            val dateToNotify = DateTime(
                myDay,
                myMonth,
                myYear,
                myHour,
                myMinute
            )
            var hasErro : Boolean = false

            if (txtTitulo.text.isNullOrEmpty()) {
                txtTitulo.error = getString(R.string.hasError)
                hasErro = true
            } else {
                titulo = txtTitulo.text.toString()
            }

            if(txtResumo.text.isNullOrEmpty()) {
                txtResumo.error = getString(R.string.hasError)
                hasErro = true
            } else {
                resumo = txtResumo.text.toString()
            }

            if(txtDescricao.text.isNullOrEmpty()) {
                txtDescricao.error = getString(R.string.hasError)
                hasErro = true
            } else {
                descricao = txtDescricao.text.toString()
            }

            if(!txtDateTime.text.isNullOrEmpty()) {
                dateTime = txtDateTime.text.toString()
            }

            if (txtDateTime.text.isEmpty() && periodicidade != 0) {
                // Periodicidade é diferente de "Nenhum" e não escolhei um dateTime
                hasErro = true
                Toast.makeText(this, "Escolha uma data/hora para o primeiro alarme!", Toast.LENGTH_SHORT).show()
            }

            if(!hasErro && periodicidade != 0) {
                // Se não tem erro nos outros campos, verifica se é uma data valida para notificao
                val currentDate = Date()
                Log.println(Log.INFO, "Date", "Data nova: ${dateToNotify.toDate()} - Data atual: ${currentDate}")
                if(dateToNotify.toDate().before(currentDate)) {
                    hasErro = true
                    Toast.makeText(this, "Escolha uma data/hora que seja depois da data atual!", Toast.LENGTH_SHORT).show()
                }
            }

            when(action) {
                "editar" -> {
                    try {
                        if(!hasErro) {
                            val oldNote = db.noteDAO().getNoteById(position)
                            var nota = Note(
                                position,
                                titulo,
                                resumo,
                                descricao,
                                oldNote.criadoEm,
                                periodicidade,
                                dateToNotify,
                                oldNote.broadcastId
                            )
                            updateNotificationAlert(oldNote, nota)
                            db.noteDAO().update(nota)
                            finish()
                        }

                    } catch (e: Exception) {
                        Toast.makeText(this, "Erro ao atualizar a nota", Toast.LENGTH_SHORT).show()
                    } finally {
                        if(!hasErro) finish()
                    }
                }
                "inserir" -> {
                    try {
                        if(!hasErro) {
                            var nota = Note(
                                titulo = titulo,
                                resumo = resumo,
                                descricao = descricao,
                                criadoEm = Date(),
                                periodicidade = periodicidade,
                                dataHoraAlerta = dateToNotify,
                                broadcastId = System.currentTimeMillis()
                            )
                            val noteId = db.noteDAO().insert(nota)

                            // Cria o alerta
                            if(periodicidade !== 0) createNotification(noteId.toInt(), nota)
                            Toast.makeText(this, "Nota criada com sucesso!", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(this, "Erro ao salvar nova nota", Toast.LENGTH_SHORT).show()
                    } finally {
                        if(!hasErro) finish()
                    }
                }
            }
        }
    }

    private fun createNotification(noteId : Int, note : Note) {
        Log.println(Log.INFO, "Notificação", "Criando primeiro alerta para a nota")
        val broadcastIntent = Intent(this, Alarm::class.java)
        broadcastIntent.putExtra("noteId", noteId)
        val pendingIntent = PendingIntent.getBroadcast(
            this,
            note.broadcastId.toInt(),
            broadcastIntent,
            0
        )
        // Calculando os ms para a notificação
        var millisecond = note.dataHoraAlerta.toDate().time
        Log.println(Log.INFO, "Notificação", "Notificação criada para ${note.dataHoraAlerta.toDate()}")
        val alarm = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarm.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, millisecond, pendingIntent)
    }

    private fun updateNotificationAlert(oldNote : Note, note: Note) {
        val broadcastIntent = Intent(this, Alarm::class.java)
        broadcastIntent.putExtra("noteId", note.id)
        val pendingIntent = PendingIntent.getBroadcast(
            this,
            note.broadcastId.toInt(),
            broadcastIntent,
            0
        )
        val alarm = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        // Antes era para notificar, agora não é mais
        if(oldNote.periodicidade !== 0 || note.periodicidade == 0) {
            alarm.cancel(pendingIntent)
        }
        // Se a periodicidade da nova notificação for diferente de nenhum, cria a notificação
        if (note.periodicidade !== 0) {
            var millisecond = note.dataHoraAlerta.toDate().time
            alarm.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, millisecond, pendingIntent)
        }

    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        myDay = dayOfMonth
        myYear = year
        myMonth = month + 1
        val calendar: Calendar = Calendar.getInstance()
        hour = calendar.get(Calendar.HOUR_OF_DAY)
        minute = calendar.get(Calendar.MINUTE)
        val timePickerDialog = TimePickerDialog(this, this, hour, minute,
            DateFormat.is24HourFormat(this))
        timePickerDialog.show()
    }
    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        myHour = hourOfDay
        myMinute = minute
        txtDateTime.text = "$myDay/$myMonth/$myYear - $myHour:$myMinute h";
    }

    fun setupDatabase() {
        // Criação do Banco de Dados
        db = AppDatabase.getInstance(this)
    }


}