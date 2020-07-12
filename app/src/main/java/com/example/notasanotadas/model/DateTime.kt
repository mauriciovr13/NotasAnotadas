package com.example.notasanotadas.model

class DateTime(_day : Int, _month : Int, _year : Int, _hour : Int, _minute : Int) {

    var day = _day
    var month: Int = _month
    var year: Int = _year
    var hour: Int = _hour
    var minute: Int = _minute

    override fun toString(): String {
        return "$day/$month/$year - $hour:$minute h"
    }

    fun formatToSave(): String {
        return "$day-$month-$year-$hour-$minute"
    }


}