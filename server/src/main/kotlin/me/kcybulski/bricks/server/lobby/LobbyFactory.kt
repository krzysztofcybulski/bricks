package me.kcybulski.bricks.server.lobby

import me.kcybulski.bricks.events.EventBus
import java.util.UUID.randomUUID

class LobbyFactory(
    private val eventBus: EventBus,
    private val nameGenerator: () -> String
) {

    fun create(name: String? = null): Lobby =
        OpenLobby(name ?: coolName(), randomUUID(), eventBus)

    private fun coolName() = nameGenerator()
        .lowercase()
        .replace(" ", "-")
        .filter { it.isLetterOrDigit() || it == '-' }

}
