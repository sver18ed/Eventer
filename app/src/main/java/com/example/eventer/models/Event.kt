package com.example.eventer.models

import java.io.Serializable

class Event(
    val id: String = "",
    val title: String = "",
    val content: String = ""
) : Serializable

{
    override fun toString() = title
}