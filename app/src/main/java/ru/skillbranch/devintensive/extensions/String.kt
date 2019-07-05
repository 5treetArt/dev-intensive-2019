package ru.skillbranch.devintensive.extensions

fun String.truncate(len: Int = 16): String {
    val trimmedStr = this.trim()

    if (trimmedStr.length - 1 <= len)
        return trimmedStr

    return trimmedStr.substring(0, len + 1) + "..."
}

fun String.stripHtml(): String = this.replace(htmlTags, "").replace(manyWhitespaces, " ")

val htmlTags = Regex("(<.+?>)|(</.+?>)|(<.+?/>)|(&#[0-9]+?;)|(&[a-z]+?;)") //<[^>]*>
val manyWhitespaces = Regex(" +")