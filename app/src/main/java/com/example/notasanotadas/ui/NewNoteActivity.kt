package com.example.notasanotadas.ui

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.text.format.DateFormat
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.notasanotadas.R
import com.example.notasanotadas.model.DateTime
import com.example.notasanotadas.model.ListNotes
import com.example.notasanotadas.model.database.AppDatabase
import com.example.notasanotadas.model.database.Note
import kotlinx.android.synthetic.main.nova_nota_activity.*
import java.lang.Exception
import java.util.*
import kotlin.reflect.typeOf

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
                android.R.layout.simple_spinner_item, periodicityList)
            spinner.adapter = adapter
        }

        if (position !== -1) {
            val nota = db.noteDAO().getNoteById(position) as Note

            txtTitulo.setText(nota.titulo)
            txtResumo.setText(nota.resumo)
            txtDescricao.setText(nota.descricao)
            txtDateTime.text = nota.dataHoraAlerta.toString()
            spinner.setSelection(nota.periodicidade)

            // Alterando o nome do botao
            btnNote.setText(R.string.edit)
        }
        txtTitulo.error = "Erro"

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

            when(action) {
                "editar" -> {
                    try {
                        val oldNote = db.noteDAO().getNoteById(position)
                        var nota = Note(
                            position,
                            titulo,
                            resumo,
                            descricao,
                            oldNote.criadoEm,
                            periodicidade,
                            DateTime(
                                myDay,
                                myMonth,
                                myYear,
                                myHour,
                                myMinute
                            )
                        )
                        if(!hasErro) {
                            db.noteDAO().update(nota)
                            finish()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(this, "Erro ao atualizar a nota", Toast.LENGTH_SHORT).show()
                    }
                }
                "inserir" -> {
                    try {
                        var nota = Note(
                            titulo = titulo,
                            resumo = resumo,
                            descricao = descricao,
                            criadoEm = Date(),
                            periodicidade = periodicidade,
                            dataHoraAlerta = DateTime(
                                myDay,
                                myMonth,
                                myYear,
                                myHour,
                                myMinute
                            )
                        )
                        if(!hasErro) {
                            db.noteDAO().update(nota)
                            finish()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(this, "Erro ao salvar nova nota", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        myDay = dayOfMonth
        myYear = year
        myMonth = month + 1
        val calendar: Calendar = Calendar.getInstance()
        hour = calendar.get(Calendar.HOUR)
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