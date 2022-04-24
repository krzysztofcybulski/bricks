package me.kcybulski.bricks.server.lobby

import com.github.javafaker.Faker
import me.kcybulski.bricks.events.EventBus
import java.util.UUID.randomUUID

class LobbyFactory(
    private val eventBus: EventBus,
    private val faker: Faker = Faker()
) {

    fun create(): Lobby = OpenLobby(coolName(), randomUUID(), eventBus)

    private fun coolName() = faker.food().dish()
        .lowercase()
        .replace(" ", "-")
        .filter { it.isLetter() || it == '-' }

}
