package com.example.eventer.models

import java.io.Serializable

class Event(
    val id: String = "",
    val title: String = "",
    val start_date: String = "",
    val end_date: String = "",
    val start_time: String = "",
    val end_time: String = "",
    val description: String = "",
    val created_by: String = ""

) : Serializable

{
    override fun toString() = title
}