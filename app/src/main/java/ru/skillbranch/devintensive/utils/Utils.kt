package ru.skillbranch.devintensive.utils

object Utils {
    fun parseFullName(fullName:String?):Pair<String?, String?> =
        fullName?.split(" ")
                ?.filter { !it.isBlank() }
                ?.windowed(2, 2)
                ?.map { it.getOrNull(0) to it.getOrNull(1) }
                ?.getOrNull(0)
                ?: null to null

    fun transliteration(payload: String, divider:String = " "): String =
        payload.toCharArray()
               .joinToString(separator = "") {
                   val key: Char = it.toLowerCase()
                   if (map.containsKey(key))
                       if (it.isUpperCase()) (map[key] ?: "").capitalize() else map[key] ?: ""
                   else
                       it.toString()
               }
               .replace(" ", divider)


    fun toInitials(firstName: String?, lastName: String?): String? =
        listOf(firstName, lastName)
            .map { firstLetterOrNull(it) }
            .fold("", {initials: String, letter -> if (letter == null) initials else initials.plus(letter.toUpperCase())})
            .ifEmpty { null }

    private fun firstLetterOrNull(str: String?): Char? =
        if (!str.isNullOrBlank())
            str[0]
        else
            null

    private val map: Map<Char, String> = mapOf(
        'а' to "a",
        'б' to "b",
        'в' to "v",
        'г' to "g",
        'д' to "d",
        'е' to "e",
        'ё' to "e",
        'ж' to "zh",
        'з' to "z",
        'и' to "i",
        'й' to "i",
        'к' to "k",
        'л' to "l",
        'м' to "m",
        'н' to "n",
        'о' to "o",
        'п' to "p",
        'р' to "r",
        'с' to "s",
        'т' to "t",
        'у' to "u",
        'ф' to "f",
        'х' to "h",
        'ц' to "c",
        'ч' to "ch",
        'ш' to "sh",
        'щ' to "sh'",
        'ъ' to "",
        'ы' to "i",
        'ь' to "",
        'э' to "e",
        'ю' to "yu",
        'я' to "ya")
}