package me.kcybulski.bricks.server.api.lobbies

import me.kcybulski.bricks.gamehistory.GameHistoriesFacade
import me.kcybulski.bricks.gamehistory.GameState.ENDED
import me.kcybulski.bricks.gamehistory.GameView
import me.kcybulski.bricks.server.views.lobbies.LobbyView
import me.kcybulski.bricks.server.views.lobbies.LobbyView.Status.CLOSED
import me.kcybulski.bricks.server.views.lobbies.LobbyView.Status.IN_GAME
import me.kcybulski.bricks.server.views.lobbies.LobbyView.Status.OPEN

data class LobbyWithStateResponse(
    val name: String,
    val image: String,
    val players: List<PlayerResponse>,
    val status: String,
    val games: List<EndedGameResponse>,
) {

    val points = games
        .groupBy(EndedGameResponse::winner)
        .mapValues { (_, value) -> value.count() }

}

fun LobbyView.toResponse(gameHistoriesFacade: GameHistoriesFacade) = when (status) {
    OPEN -> toResponse("OPEN")
    IN_GAME -> toResponse("IN_GAME", gameHistoriesFacade.games(id))
    CLOSED -> toResponse("ENDED", gameHistoriesFacade.games(id))
}

private fun LobbyView.toResponse(status: String, games: List<GameView> = emptyList()) =
    LobbyWithStateResponse(
        name = name,
        image = image,
        players = emptyList(),
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

data class PlayerResponse(
    val name: String,
    val image: String
)

private fun GameView.toResponse() = EndedGameResponse(
    id = id.toString(),
    winner = if (state == ENDED) winner ?: "TIE" else null,
    players = players.toList(),
    size = size,
    state = state.toString()
)
