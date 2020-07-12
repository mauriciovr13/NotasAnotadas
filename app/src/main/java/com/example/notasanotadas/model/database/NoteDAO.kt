package com.example.notasanotadas.model.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface NoteDAO {
    @Query("SELECT * FROM note")
    fun getAllNotes() : List<Note>

    @Query("SELECT * FROM note")
    fun getAll() : LiveData<List<Note>>

    @Query("SELECT * FROM note WHERE id=:id")
    fun getNoteById(id : Int) : Note

    @Insert
    fun insertAll(vararg notes: Note)

    @Insert
    fun insert(note : Note)

    @Delete
    fun delete(note: Note)

    @Update
    fun update(vararg notes: Note)
}