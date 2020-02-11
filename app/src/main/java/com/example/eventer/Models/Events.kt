package com.example.eventer.models

data class Events(
    val id: Int,
    var title: String,
    var content: String
) {
    override fun toString() = title
}