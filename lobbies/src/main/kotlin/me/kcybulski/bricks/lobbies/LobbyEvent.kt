package me.kcybulski.bricks.lobbies

import me.kcybulski.bricks.tournament.TournamentSettings

interface LobbyEvent {
    val lobbyId: LobbyId
}

data class PlayerJoinedToLobby(
    override val lobbyId: LobbyId,
    val player: String
) : LobbyEvent

data class PlayerLeftLobby(
    override val lobbyId: LobbyId,
    val player: String
) : LobbyEvent

data class LobbyAdded(
    override val lobbyId: LobbyId,
    val lobbyName: String
) : LobbyEvent

data class LobbyDeleted(
    override val lobbyId: LobbyId
) : LobbyEvent

data class LobbyStartedTournament(
    override val lobbyId: LobbyId,
    val settings: TournamentSettings
) : LobbyEvent

data class LobbyClosed(
    override val lobbyId: LobbyId
) : LobbyEvent
