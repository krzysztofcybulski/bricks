package me.kcybulski.bricks.server.lobby

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import me.kcybulski.bricks.server.PlayerConnection
import me.kcybulski.bricks.tournament.TournamentFacade
import me.kcybulski.bricks.tournament.TournamentResult
import me.kcybulski.bricks.web.MoveMessage
import me.kcybulski.bricks.web.ReadyMessage
import ratpack.websocket.WebSocket
import java.util.concurrent.Executors.newSingleThreadExecutor

sealed class Lobby(
    val name: String
) {

    abstract fun playerNames(): List<String>

}

class OpenLobby(
    name: String
) : Lobby(name) {

    private val players: MutableList<PlayerConnection> = mutableListOf()

    fun registerPlayer(name: String, webSocket: WebSocket) {
        players += PlayerConnection(name, webSocket)
    }

    fun inProgress(tournaments: TournamentFacade) = InGameLobby(name, players, tournaments)

    override fun playerNames(): List<String> = players.map(PlayerConnection::name)

    suspend fun ready(connection: WebSocket) {
        players.find { it.webSocket == connection }?.channel?.send(ReadyMessage)
    }

    suspend fun moved(connection: WebSocket, message: MoveMessage) {
        players.find { it.webSocket == connection }?.channel?.send(message)
    }

    suspend fun healthy(connection: WebSocket) {
        players.find { it.webSocket == connection }?.healthChannel?.send(true)
    }

    suspend fun refresh() = coroutineScope {
        val toRemove = players.filter { !it.isHealthy() }
        players.removeAll(toRemove)
    }

}

class InGameLobby(
    name: String,
    private val players: List<PlayerConnection>,
    private val tournaments: TournamentFacade
) : Lobby(name) {

    private val gameScope = CoroutineScope(
        newSingleThreadExecutor()
            .asCoroutineDispatcher()
    )

    suspend fun run(): ClosedLobby =
        withContext(gameScope.coroutineContext) {
            ClosedLobby(
                name,
                tournaments.play(players) {
                    initTime = 1000
                    moveTime = 500
                },
                playerNames()
            )
        }

    override fun playerNames(): List<String> = players.map(PlayerConnection::name)

}

class ClosedLobby(
    name: String,
    val result: TournamentResult,
    val playerNames: List<String>
) : Lobby(name) {
    override fun playerNames(): List<String> = playerNames
}
