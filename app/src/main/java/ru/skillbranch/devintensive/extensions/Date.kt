package ru.skillbranch.devintensive.extensions

import java.lang.IllegalStateException
import java.text.SimpleDateFormat
import java.util.*

const val SECOND = 1000L
const val MINUTE = 60 * SECOND
const val HOUR = 60 * MINUTE
const val DAY = 24 * HOUR



fun Date.format(pattern:String="HH:mm:ss dd.MM.yy"):String {
    val dateFormat = SimpleDateFormat(pattern, Locale("ru"))
    return dateFormat.format(this)
}

fun Date.add(value:Int, units: TimeUnits = TimeUnits.SECOND):Date {
    var time = this.time

     time += when(units){
        TimeUnits.SECOND-> value * SECOND
        TimeUnits.MINUTE-> value * MINUTE
        TimeUnits.HOUR-> value * HOUR
        TimeUnits.DAY-> value * DAY
    }

    this.time = time

    return this
}

fun Date.humanizeDiff(date: Date = Date()): String {
    //TODO был только что,
    // был несколько секунд назад,
    // был n секунд назад,
    // был несколько минут назад,
    // был n минут назад,
    // был несколько часов назад,
    // был n часов назад,
    // был несколько дней назад,
    // был n дней(дня, день) назад,
    // был несколько несколько назад,
    // был n недель назад,
    // был более года назад,
    // был n лет(года, год) назад,

    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
}

enum class TimeUnits{
    SECOND,
    MINUTE,
    HOUR,
    DAY
}