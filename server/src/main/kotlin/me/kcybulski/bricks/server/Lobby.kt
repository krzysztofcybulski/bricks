package me.kcybulski.bricks.server

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext
import me.kcybulski.bricks.tournament.TournamentFacade
import me.kcybulski.bricks.tournament.TournamentResult
import me.kcybulski.bricks.web.MoveMessage
import me.kcybulski.bricks.web.ReadyMessage
import ratpack.websocket.WebSocket

class Lobby(
    val size: Int,
    private val tournaments: TournamentFacade
) {

    private val players: MutableList<PlayerConnection> = mutableListOf()
    private var results: TournamentResult? = null

    private val gameScope = CoroutineScope(newSingleThreadContext("game"))

    suspend fun registerPlayer(name: String, webSocket: WebSocket) = coroutineScope {
        players += PlayerConnection(name, webSocket)
        if (players.size == size) {
            runGame()
        }
    }

    private fun runGame() = gameScope.launch {
        results = tournaments.play(players) {
            initTime = 1000
            moveTime = 500
        }
    }

    suspend fun ready(connection: WebSocket) {
        players.find { it.webSocket == connection }?.channel?.send(ReadyMessage)
    }

    suspend fun moved(connection: WebSocket, message: MoveMessage) {
        players.find { it.webSocket == connection }?.channel?.send(message)
    }

}
