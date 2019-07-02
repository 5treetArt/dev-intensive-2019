package ru.skillbranch.devintensive.utils

import java.lang.Exception

object Utils {
    fun parseFullName(fullName:String?):Pair<String?, String?>{
        val parts : List<String>? = fullName?.split(" ")?.filter { s: String -> !s.isBlank() }

        var firstName = parts?.getOrNull(0)
        if (firstName == "")
            firstName = null

        var lastName = parts?.getOrNull(1)
        if (lastName == "")
            lastName = null

        return firstName to lastName
    }

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


    fun toInitials(firstName: String?, lastName: String?): String? {

        val firstLet = firstLetterOrNull(firstName)
        val secondLet = firstLetterOrNull(lastName)

        if (firstLet == null && secondLet == null)
            return null

        return ((firstLet ?: "").toString() + (secondLet ?: "").toString()).toUpperCase()
    }

    private fun firstLetterOrNull(str: String?): Char?{
        if (!str.isNullOrBlank())
            return str[0]
        return null
    }

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