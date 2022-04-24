package me.kcybulski.bricks.server.lobby

import java.util.UUID

interface LobbyEvent {
    val lobbyName: String
    val lobbyId: UUID
}

data class PlayerJoinedToLobby(
    override val lobbyName: String,
    override val lobbyId: UUID,
    val player: String
) : LobbyEvent

data class PlayerLeftLobby(
    override val lobbyName: String,
    override val lobbyId: UUID,
    val player: String
) : LobbyEvent

data class LobbyAdded(
    override val lobbyName: String,
    override val lobbyId: UUID
) : LobbyEvent
