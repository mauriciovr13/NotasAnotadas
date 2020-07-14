package com.example.notasanotadas.model.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.notasanotadas.model.DateTime
import java.util.*

@Entity
data class Note(
    @PrimaryKey(autoGenerate = true) val id : Int? = null,
    @ColumnInfo(name = "titulo") val titulo : String,
    @ColumnInfo(name = "resumo") val resumo : String,
    @ColumnInfo(name = "descricao") val descricao : String,
    @ColumnInfo(name = "criadoEm") val criadoEm : Date,
    @ColumnInfo(name = "periodicidade") var periodicidade : Int,
    @ColumnInfo(name = "dataHoraAlerta") val dataHoraAlerta : DateTime,
    @ColumnInfo(name = "broacastId") val broadcastId : Long
)