package me.kcybulski.bricks.server.lobby

import me.kcybulski.bricks.api.Algorithm
import me.kcybulski.bricks.server.HealthStatus
import me.kcybulski.bricks.server.PlayerConnection
import me.kcybulski.bricks.tournament.TournamentFacade
import me.kcybulski.bricks.tournament.TournamentResult
import me.kcybulski.bricks.tournament.TournamentSettings
import me.kcybulski.bricks.web.MoveMessage
import me.kcybulski.bricks.web.ReadyMessage
import mu.KotlinLogging
import ratpack.websocket.WebSocket
import java.util.UUID

sealed class Lobby(
    val name: String,
    val id: UUID
) {

    abstract fun playerNames(): List<String>

}

class OpenLobby(
    name: String,
    id: UUID
) : Lobby(name, id) {

    private val logger = KotlinLogging.logger {}
    private val players: MutableList<Algorithm> = mutableListOf()

    fun registerPlayer(name: String, webSocket: WebSocket) {
        logger.info { "${this@OpenLobby.name} - Registered player $name" }
        players += PlayerConnection(name, webSocket)
    }

    fun registerBot(algorithm: Algorithm) {
        players += algorithm
    }

    fun inProgress(tournaments: TournamentFacade, settings: TournamentSettings) =
        InGameLobby(name, id, settings, players, tournaments)

    override fun playerNames(): List<String> = players.map { it.identity.name }

    suspend fun ready(connection: WebSocket) {
        findWebsocket(connection)?.channel?.send(ReadyMessage)
    }

    suspend fun moved(connection: WebSocket, message: MoveMessage) {
        findWebsocket(connection)?.channel?.send(message)
    }

    suspend fun healthy(connection: WebSocket) {
        findWebsocket(connection)?.healthChannel?.send(true)
    }

    suspend fun getHealthStatuses(): Map<PlayerConnection, HealthStatus> =
        players
            .filterIsInstance<PlayerConnection>()
            .associateWith { it.healthStatus() }

    suspend fun kick(playerConnection: PlayerConnection) {
        players.remove(playerConnection)
    }

    private fun findWebsocket(connection: WebSocket) =
        players
            .filterIsInstance<PlayerConnection>()
            .find { it.webSocket == connection }

}

class InGameLobby(
    name: String,
    id: UUID,
    private val settings: TournamentSettings,
    private val players: List<Algorithm>,
    private val tournaments: TournamentFacade
) : Lobby(name, id) {

    suspend fun run(): ClosedLobby =
        ClosedLobby(
            name,
            id,
            tournaments.play(id, players, settings),
            playerNames()
        )

    override fun playerNames(): List<String> = players.map { it.identity.name }

}

class ClosedLobby(
    name: String,
    id: UUID,
    val result: TournamentResult,
    val playerNames: List<String>
) : Lobby(name, id) {
    override fun playerNames(): List<String> = playerNames
}
