package com.example.eventer.models

data class User (
    val id: Int,
    var name: String,
    var email: String,
    var info: String,
    var friends: Array<User> = emptyArray()


) {
    override fun toString() = name
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as User

        if (id != other.id) return false
        if (name != other.name) return false
        if (email != other.email) return false
        if (info != other.info) return false
        if (!friends.contentEquals(other.friends)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + name.hashCode()
        result = 31 * result + email.hashCode()
        result = 31 * result + info.hashCode()
        result = 31 * result + friends.contentHashCode()
        return result
    }
}