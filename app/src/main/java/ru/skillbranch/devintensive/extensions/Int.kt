package ru.skillbranch.devintensive.extensions

fun Int.toBytesArray(): Array<Int>{
    val bytes = arrayOf(0,0,0,0)
    bytes[0] = this and 0xFF
    bytes[1] = (this shr 8) and 0xFF
    bytes[2] = (this shr 16) and 0xFF
    bytes[3] = (this shr 24) and 0xFF

    return bytes
}