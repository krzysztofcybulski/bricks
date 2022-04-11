package me.kcybulski.bricks.server.api

import me.kcybulski.bricks.game.EndedGame
import me.kcybulski.bricks.game.WonGame
import me.kcybulski.bricks.server.lobby.ClosedLobby
import me.kcybulski.bricks.server.lobby.InGameLobby
import me.kcybulski.bricks.server.lobby.Lobby
import me.kcybulski.bricks.server.lobby.OpenLobby

data class LobbyWithStateResponse(
    val name: String,
    val playerNames: List<String>,
    val status: String,
    val games: List<EndedGameResponse>,
) {

    val points = games
        .groupBy(EndedGameResponse::winner)
        .mapValues { (_, value) -> value.count() }

}

fun Lobby.toResponse() = when (this) {
    is OpenLobby -> toResponse("OPEN")
    is InGameLobby -> toResponse("IN_GAME")
    is ClosedLobby -> toResponse("ENDED", result.games)
}

private fun Lobby.toResponse(status: String, games: List<EndedGame> = emptyList()) =
    LobbyWithStateResponse(
        name,
        playerNames(),
        status,
        games.map(EndedGame::toResponse)
    )

data class EndedGameResponse(
    val id: String,
    val winner: String,
    val players: List<String>
)

private fun EndedGame.toResponse() = EndedGameResponse(
    id = id.toString(),
    winner = if (this is WonGame) winner.name else "TIE",
    players = players.names().toList()
)
