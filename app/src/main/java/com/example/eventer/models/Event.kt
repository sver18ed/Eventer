package com.example.eventer.models

import java.io.Serializable

class Event(
    val id: String = "",
    val title: String = "",
    val content: String = "",
    val created_by: String = ""

) : Serializable

{
    override fun toString() = title
}