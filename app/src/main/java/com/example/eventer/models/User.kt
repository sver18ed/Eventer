package com.example.eventer.models

import java.io.Serializable

data class User (
    val email: String = "",
    val password: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val id: String = ""

) : Serializable