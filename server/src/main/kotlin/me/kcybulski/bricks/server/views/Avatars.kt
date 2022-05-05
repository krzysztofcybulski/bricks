package me.kcybulski.bricks.server.views

import me.kcybulski.bricks.api.Identity
import me.kcybulski.bricks.lobbies.LobbyId

object Avatars {

    fun generateForPlayer(identity: Identity): String = avatarUrl("avataaars", identity.name)

    fun generateForLobby(id: LobbyId): String = avatarUrl("bottts", id.raw.toString())

    private fun avatarUrl(type: String, seed: String) =
        "https://avatars.dicebear.com/api/${type}/${seed}.svg?style=circle"

}