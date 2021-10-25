package me.kcybulski.bricks.events

class EventBus {

    fun <T: Any> send(event: T) {
        println(event)
    }

}
