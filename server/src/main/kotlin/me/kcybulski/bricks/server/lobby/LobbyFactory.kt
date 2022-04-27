package me.kcybulski.bricks.server.lobby

import me.kcybulski.bricks.events.EventBus
import java.util.UUID.randomUUID

class LobbyFactory(
    private val eventBus: EventBus,
    private val nameGenerator: () -> String
) {

    fun create(): Lobby = OpenLobby(coolName(), randomUUID(), eventBus)

    private fun coolName() = nameGenerator()
        .lowercase()
        .replace(" ", "-")
        .filter { it.isLetterOrDigit() || it == '-' }

}
