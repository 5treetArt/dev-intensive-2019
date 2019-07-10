package ru.skillbranch.devintensive.extensions

import java.lang.Math.abs
import java.lang.Math.round
import java.text.SimpleDateFormat
import java.util.*

const val SECOND = 1000L
const val MINUTE = 60 * SECOND
const val HOUR = 60 * MINUTE
const val DAY = 24 * HOUR

fun Date.format(pattern:String="HH:mm:ss dd.MM.yy"):String =
    SimpleDateFormat(pattern, Locale("ru")).format(this)

fun Date.add(value:Int, units: TimeUnits = TimeUnits.SECOND):Date = apply{
    this.time += when(units){
        TimeUnits.SECOND-> value * SECOND
        TimeUnits.MINUTE-> value * MINUTE
        TimeUnits.HOUR-> value * HOUR
        TimeUnits.DAY-> value * DAY
    }
}

fun Date.humanizeDiff(date: Date = Date()): String {

    val diff = date.time - this.time

    return when(abs(diff)) {
        in 0..(1 * SECOND) -> if(diff >= 0) "только что" else "скоро"
        in (1 * SECOND)..(360 * DAY) -> String.format(getFormat(diff), toUnitsString(abs(diff)))
        else -> if(diff > 0) "более года назад" else "более чем через год"
    }
}

private fun getFormat(diff: Long): String = when (diff) {
    in 0..Long.MAX_VALUE -> "%s назад"
    else -> "через %s"
}

private fun toUnitsString(millis: Long): String = when(millis) {
    in (1 * SECOND)..(45 * SECOND) -> "несколько секунд"
    in (45 * SECOND)..(75 * SECOND) -> "минуту"
    in (75 * SECOND)..(45 * MINUTE) -> TimeUnits.MINUTE.pluralMillis(millis)//.plural(TimeUnits.MINUTE.getFromMillis(millis).toInt())
    in (45 * MINUTE)..(75 * MINUTE) -> "час"
    in (75 * MINUTE)..(22 * HOUR) -> TimeUnits.HOUR.pluralMillis(millis)
    in (22 * HOUR)..(26 * HOUR) -> "день"
    in (26 * HOUR)..(360 * DAY) -> TimeUnits.DAY.pluralMillis(millis)
    else -> ""
}

enum class TimeUnits{
    SECOND,
    MINUTE,
    HOUR,
    DAY;

    fun plural(value: Int): String {

        val cases = getDeclinedWords()

        return when(value % 100) {
            in 11..14 -> "" + value + " " + cases[0]
            else ->
                when(value % 10) {
                    0, in 5..9 -> "" + value + " " + cases[0]
                    1 -> "" + value + " " + cases[1]
                    in 2..4 -> "" + value + " " + cases[2]
                    else -> ""
                }
        }
    }

    fun pluralMillis(diff: Long): String = plural(this.getFromMillis(diff).toInt())

    private fun getFromMillis(millis: Long): Long = round(millis.toDouble()/getInMillis())

    private fun getInMillis(): Long = when(this) {
        SECOND -> ::SECOND.get()
        MINUTE -> ::MINUTE.get()
        HOUR -> ::HOUR.get()
        DAY -> ::DAY.get()
    }

    private fun getDeclinedWords() = when(this) {
        SECOND -> arrayOf("секунд", "секунду", "секунды")
        MINUTE -> arrayOf("минут", "минуту", "минуты")
        HOUR -> arrayOf("часов", "час", "часа")
        DAY -> arrayOf("дней", "день", "дня")
    }
}