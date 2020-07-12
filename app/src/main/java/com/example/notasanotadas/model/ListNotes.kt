package com.example.notasanotadas.model

import kotlin.collections.ArrayList

class ListNotes {
    companion object {
        var notes : ArrayList<Note> = ArrayList()

        fun addNote(titulo : String, resumo : String, descricao : String, periodicidade : Int,
                    dataHoraAlerta : DateTime
        ) {
            val id = notes.size
            val n = Note(
                id,
                titulo,
                resumo,
                descricao,
                periodicidade,
                dataHoraAlerta
            )
            notes.add(n)
        }

        fun deleteNote(index : Int): Boolean {
            return try {
                notes.removeAt(index)
                true
            } catch (e : IllegalArgumentException) {
                false
            }
        }

        fun updateNote(id: Int, titulo : String, resumo : String, descricao : String, periodicidade : Int,
                       dataHoraAlerta : DateTime) {
            notes[id].titulo = titulo
            notes[id].resumo = resumo
            notes[id].descricao = descricao
            notes[id].periodicidade = periodicidade
            notes[id].dataHoraAlerta = dataHoraAlerta

        }
    }

}