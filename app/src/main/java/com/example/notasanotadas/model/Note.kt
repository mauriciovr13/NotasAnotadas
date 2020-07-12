package com.example.notasanotadas.model

import com.example.notasanotadas.model.DateTime
import java.util.*

class Note(_id : Int, _titulo : String, _resumo : String, _descricao : String, _periodicidade : Int,
           _dataHoraAlerta : DateTime
) {
    var id : Int = _id
    var titulo : String = _titulo
    var resumo : String = _resumo
    var descricao : String = _descricao
    var criadoEm : Date = Date()
    var periodicidade : Int = _periodicidade
    var dataHoraAlerta : DateTime = _dataHoraAlerta

}