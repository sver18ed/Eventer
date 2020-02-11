package com.example.eventer.models

data class User (
    val id: Int,
    var name: String,
    var email: String,
    var info: String
) {
    override fun toString() = name
}