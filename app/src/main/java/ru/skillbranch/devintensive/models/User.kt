package ru.skillbranch.devintensive.models

import ru.skillbranch.devintensive.utils.Utils
import java.util.*

data class User (
        val id: String,
        var firstName: String?,
        var lastName: String?,
        var avatar: String?,
        var rating: Int = 0,
        var respect: Int = 0,
        val lastVisit: Date? = Date(),
        val isOnline: Boolean = false
) {
    constructor(id:String, firstName: String?, lastName: String?) : this(
        id = id,
        firstName = firstName,
        lastName = lastName,
        avatar = null
    )

    private constructor(builder: Builder) :
            this(builder.id, builder.firstName, builder.lastName, builder.avatar,
                builder.rating, builder.respect, builder.lastVisit, builder.isOnline)

    constructor(id:String): this(id, "John", "Doe")

    init {
        println("It's Alive!!! \n${if(lastName==="Doe") "His name is $firstName $lastName" else "And his name is $firstName $lastName!!!" }\n" )
    }

    companion object Factory {
        private var lastId: Int = -1
        fun makeUser(fullName:String?) : User{
            lastId++

            val (firstName, lastName) = Utils.parseFullName(fullName)

            return User(id = "$lastId", firstName = firstName, lastName = lastName)

        }
    }

    class Builder () {
        var id: String = ""
        var firstName: String? = null
        var lastName: String? = null
        var avatar: String? = null
        var rating: Int = 0
        var respect: Int = 0
        var lastVisit: Date? = Date()
        var isOnline: Boolean = false

        fun id (value: String) = apply {id = value}
        fun firstName (value: String?) = apply {firstName  = value}
        fun lastName (value: String?) = apply {lastName  = value}
        fun avatar (value: String?) = apply {avatar  = value}
        fun rating (value: Int) = apply {rating = value}
        fun respect (value: Int) = apply {respect = value}
        fun lastVisit (value: Date?) = apply {lastVisit  = value}
        fun isOnline (value: Boolean) = apply {isOnline = value}

        fun build(): User {
            return User(this)
        }
    }
}