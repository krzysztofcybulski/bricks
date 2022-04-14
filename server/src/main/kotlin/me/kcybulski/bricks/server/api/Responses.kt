package me.kcybulski.bricks.server.api

import me.kcybulski.bricks.gamehistory.GameHistoriesFacade
import me.kcybulski.bricks.gamehistory.GameState.ENDED
import me.kcybulski.bricks.gamehistory.GameView
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

fun Lobby.toResponse(gameHistoriesFacade: GameHistoriesFacade) = when (this) {
    is OpenLobby -> toResponse("OPEN")
    is InGameLobby -> toResponse("IN_GAME", gameHistoriesFacade.games(id))
    is ClosedLobby -> toResponse("ENDED", gameHistoriesFacade.games(id))
}

private fun Lobby.toResponse(status: String, games: List<GameView> = emptyList()) =
    LobbyWithStateResponse(
        name = name,
        playerNames = playerNames(),
        status = status,
        games = games.map(GameView::toResponse)
    )

data class EndedGameResponse(
    val id: String,
    val winner: String?,
    val players: List<String>,
    val size: Int,
    val state: String
)

data class BotResponse(
    val name: String
)

private fun GameView.toResponse() = EndedGameResponse(
    id = id.toString(),
    winner = if (state == ENDED) winner ?: "TIE" else null,
    players = players.toList(),
    size = size,
    state = state.toString()
)
