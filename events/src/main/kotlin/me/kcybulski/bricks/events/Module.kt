package me.kcybulski.bricks.events

interface Module {

    fun register(eventBus: EventBus, commandBus: CommandBus)

}