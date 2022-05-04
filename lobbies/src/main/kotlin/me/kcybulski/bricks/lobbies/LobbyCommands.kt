package me.kcybulski.bricks.lobbies

import me.kcybulski.bricks.api.Algorithm
import me.kcybulski.bricks.tournament.TournamentSettings

data class CreateLobbyCommand(
    val name: String? = null
)

data class StartTournamentCommand(
    val id: LobbyId,
    val settings: TournamentSettings
)

data class DeleteLobbyCommand(
    val id: LobbyId
)

data class JoinLobbyCommand(
    val id: LobbyId,
    val algorithm: Algorithm
)