package me.kcybulski.bricks.server

import com.github.javafaker.Faker

class LobbyFactory(
    private val faker: Faker = Faker()
) {

    fun create(): Lobby = OpenLobby(coolName())

    private fun coolName() = faker.dog().breed()
        .lowercase()
        .replace(" ", "-")

}
