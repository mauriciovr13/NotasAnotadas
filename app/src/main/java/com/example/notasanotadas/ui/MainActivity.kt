package com.example.notasanotadas.ui

import android.content.Intent
import android.os.Bundle
import android.view.ContextMenu
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.notasanotadas.ui.adapter.Adapter
import com.example.notasanotadas.R
import com.example.notasanotadas.model.database.AppDatabase
import com.example.notasanotadas.model.database.Note
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    lateinit var lista : List<Note>
    lateinit var db : AppDatabase
    lateinit var notesList : LiveData<List<Note>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        setupDatabase()
    }

    // Criação do Banco de Dados
    fun setupDatabase() {
        db = AppDatabase.getInstance(this)
    }

    override fun onResume() {
        super.onResume()
        notesList = db.noteDAO().getAll()

        notesList.observe(this, Observer {
            it?.let { list -> note_list_recyclerview.adapter = Adapter(list) }
        })
        note_list_recyclerview.layoutManager = LinearLayoutManager(this)
        registerForContextMenu(note_list_recyclerview)
    }

    override fun onRestart() {
        note_list_recyclerview.adapter?.notifyDataSetChanged()
        super.onRestart()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu,menu);
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.adicionaNota -> {
                val intent = Intent(this, NewNoteActivity::class.java)
                intent.putExtra("action", "inserir")
                startActivity(intent)
            }
            R.id.sairDoApp -> {
                //Sair do app
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateContextMenu(
        menu: ContextMenu?,
        v: View?,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)
        menuInflater.inflate(R.menu.note_menu, menu)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        Toast.makeText(this, "Cliquei", Toast.LENGTH_LONG).show()
        return super.onContextItemSelected(item)
    }
}
