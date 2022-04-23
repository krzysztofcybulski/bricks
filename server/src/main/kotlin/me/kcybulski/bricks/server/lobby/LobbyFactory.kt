package me.kcybulski.bricks.server.lobby

import com.github.javafaker.Faker
import java.util.UUID.randomUUID

class LobbyFactory(
    private val faker: Faker = Faker()
) {

    fun create(): Lobby = OpenLobby(coolName(), randomUUID())

    private fun coolName() = faker.food().dish()
        .lowercase()
        .replace(" ", "-")
        .filter { it.isLetter() || it == '-' }

}
