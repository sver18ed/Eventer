package com.example.eventer.models

import java.io.Serializable

data class User (
    val email: String = "",
    val password: String = ""

) : Serializable