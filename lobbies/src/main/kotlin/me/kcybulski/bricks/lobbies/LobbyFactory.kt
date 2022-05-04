package me.kcybulski.bricks.lobbies

import me.kcybulski.bricks.events.CommandBus
import me.kcybulski.bricks.events.EventBus
import java.util.UUID.randomUUID

internal class LobbyFactory(
    private val nameGenerator: () -> String,
    private val eventBus: EventBus,
    private val commandBus: CommandBus
) {

    fun create(command: CreateLobbyCommand): OpenLobby =
        OpenLobby(
            id = LobbyId(randomUUID()),
            name = (command.name ?: nameGenerator()).lowercase().replace(" ", "-"),
            players = emptyList(),
            eventBus = eventBus,
            commandBus = commandBus
        )

}