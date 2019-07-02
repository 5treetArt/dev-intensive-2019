package ru.skillbranch.devintensive.extensions

import java.lang.IllegalStateException
import java.lang.Math.abs
import java.lang.Math.round
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

    val diff = date.time - this.time

    val format: String = when(diff){
        in 0..Long.MAX_VALUE -> "%s назад"
        else -> "через %s"
    }

    return when(abs(diff)) {
        in 0..(1 * SECOND) -> return when{
            diff >= 0 -> "только что"
            else -> "скоро"
        }
        in (1 * SECOND)..(45 * SECOND) -> String.format(format, "несколько секунд")
        in (45 * SECOND)..(75 * SECOND) -> String.format(format, "минуту")
        in (75 * SECOND)..(45 * MINUTE) -> String.format(format, decline(diff, MINUTE, arrayOf("минут", "минуту", "минуты")))
        in (45 * MINUTE)..(75 * MINUTE) -> String.format(format, "час")
        in (75 * MINUTE)..(22 * HOUR) -> String.format(format, decline(diff, HOUR, arrayOf("часов", "час", "часа")))
        in (22 * HOUR)..(26 * HOUR) -> String.format(format, "день")
        in (26 * HOUR)..(360 * DAY) -> String.format(format, decline(diff, DAY, arrayOf("дней", "день", "дня")))
        else -> return when{
            diff > 0 -> "более года назад"
            else -> "более чем через год"
        }
    }
}

fun decline(diff: Long, timeUnit: Long, cases: Array<String>): String {
    val units = round(abs(diff).toDouble()/ timeUnit)

    return when(units % 20) {
        0L -> "" + units + " " + cases[0]
        in 5..19 -> "" + units + " " + cases[0]
        1L -> "" + units + " " + cases[1]
        in 2..4 -> "" + units + " " + cases[2]
        else -> ""
    }
}

enum class TimeUnits{
    SECOND,
    MINUTE,
    HOUR,
    DAY
}