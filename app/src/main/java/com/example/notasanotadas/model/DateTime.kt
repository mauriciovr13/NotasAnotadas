package com.example.notasanotadas.model

import java.util.*

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

    fun toDate() : Date {
        var date = Calendar.getInstance().apply {
            set(year, month-1, day, hour, minute, 0)
        }
        return Date(date.timeInMillis)
    }


}