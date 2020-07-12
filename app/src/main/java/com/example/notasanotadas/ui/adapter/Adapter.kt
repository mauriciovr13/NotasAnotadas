package com.example.notasanotadas.ui.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.notasanotadas.R
import com.example.notasanotadas.model.database.AppDatabase
import com.example.notasanotadas.model.database.Note
import com.example.notasanotadas.ui.NewNoteActivity
import kotlinx.android.synthetic.main.note_item_layout.view.*

class Adapter(private val data: List<Note>) : RecyclerView.Adapter<Adapter.MyViewHolder>() {

    lateinit var db : AppDatabase

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titulo = itemView.note_item_titulo
        val resumo = itemView.note_item_resumo
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): MyViewHolder {
        // create a new view
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.note_item_layout, parent, false) as View
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val nota = data[position]
        holder?.titulo.text = nota.titulo
        holder?.resumo.text = nota.resumo
        holder?.itemView.setOnClickListener {v: View? ->
            val intent = Intent(v?.context, NewNoteActivity::class.java)
            intent.putExtra("action", "editar")
            intent.putExtra("position", nota.id)
            v?.context?.startActivity(intent)
        }

        holder?.itemView.btnDelete.setOnClickListener {
            // colocar o dialog aqui
            val builder = AlertDialog.Builder(it.context)
            builder.setTitle("Deseja realmente apagar a nota?")
            builder.setPositiveButton(R.string.yes) { _, _ ->
                try {
                    db = AppDatabase.getInstance(it.context)
                    db.noteDAO().delete(nota)
                    notifyDataSetChanged()
                    Toast.makeText(it.context, "Nota excluída com sucesso", Toast.LENGTH_LONG).show()
                } catch (e: ArrayIndexOutOfBoundsException) {
                    Toast.makeText(it.context, "Erro ao excluir nota", Toast.LENGTH_LONG).show()
                }
            }
            builder.setNegativeButton(R.string.no) { dialog, _ ->
                dialog.dismiss()
            }

            val dialog = builder.create()
            dialog.show()
            // db = AppDatabase.getInstance(it.context)
            // Log.println(Log.INFO, "Banco", db.noteDAO().getAllNotes().size.toString())
            // Toast.makeText(it.context, "Vou excluir a nota ${nota.id}", Toast.LENGTH_LONG).show()
        }
    }

    override fun getItemCount() = data.size
}