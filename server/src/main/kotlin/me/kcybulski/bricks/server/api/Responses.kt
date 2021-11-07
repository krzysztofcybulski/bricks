package me.kcybulski.bricks.server.api

import me.kcybulski.bricks.game.EndedGame
import me.kcybulski.bricks.game.WonGame
import me.kcybulski.bricks.server.lobby.ClosedLobby
import me.kcybulski.bricks.server.lobby.InGameLobby
import me.kcybulski.bricks.server.lobby.Lobby
import me.kcybulski.bricks.server.lobby.OpenLobby

data class LobbyWithStateResponse(
    val name: String,
    val status: String
)

fun Lobby.toResponse() = when (this) {
    is OpenLobby -> LobbyWithStateResponse(name, "OPEN")
    is InGameLobby -> LobbyWithStateResponse(name, "IN_GAME")
    is ClosedLobby -> LobbyWithStateResponse(name, "ENDED")
}

data class LobbyDetailsResponse(
    val name: String,
    val players: List<String>,
    val games: List<EndedGameResponse>
) {

    val points = games.groupBy { it.winner }.mapValues { (_, value) -> value.count() }

}

data class EndedGameResponse(
    val id: String,
    val winner: String,
    val players: List<String>
)

fun Lobby.toResultsResponse() = LobbyDetailsResponse(
    name = name,
    players = playerNames(),
    games = when (this) {
        is ClosedLobby -> result.games.map(EndedGame::toResponse)
        else -> emptyList()
    }
)

private fun EndedGame.toResponse() = EndedGameResponse(
    id = id.toString(),
    winner = if (this is WonGame) winner.name else "TIE",
    players = players.names().toList()
)
