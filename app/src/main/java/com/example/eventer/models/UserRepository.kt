package com.example.eventer.models

val userRepository = UserRepository().apply{

    addUser(
        "Andreas Malmström",
        "andreas.malmstroem@gmail.com",
        "Likes to drink beer."
    )

}

class UserRepository {

    private val users = mutableListOf<User>()

    fun addUser(name: String, email: String, info: String): Int{
        val id = when {
            users.count() == 0 -> 1
            else -> users.last().id+1
        }
        users.add(User(
            id,
            name,
            email,
            info
        ))
        return id
    }

    fun getAllUsers() = users

    fun getUserById(id: Int) =
        users.find {
            it.id == id
        }

    fun deleteUserById(id: Int) =
        users.remove(
            users.find{
                it.id == id
            }
        )

    fun updateUserById(id: Int, newName: String, newEmail: String, newInfo: String) {

        getUserById(id)?.run{
            name = newName
            email = newEmail
            info = newInfo
        }
    }

}