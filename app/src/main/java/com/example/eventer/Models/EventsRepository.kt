package com.example.eventer.models

val eventsRepository = EventsRepository().apply{

    addEvents(
        "Event 1",
        "Blablalbalb"
    )
    addEvents(
        "Event 2",
        "aehhahdfhdh"
    )
}

class EventsRepository {

    private val events = mutableListOf<Events>()

    fun addEvents(title: String, content: String): Int {
        val id = when {
            events.count() == 0 -> 1
            else -> events.last().id+1
        }
        events.add(Events(
            id,
            title,
            content
        ))
        return id
    }

    fun getAllEvents() = events

    fun getEventById(id: Int) =
        events.find {
            it.id == id
        }
}