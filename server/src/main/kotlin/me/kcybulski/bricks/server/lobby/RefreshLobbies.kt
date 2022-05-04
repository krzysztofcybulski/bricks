package me.kcybulski.bricks.server.lobby

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.UNLIMITED
import kotlinx.coroutines.launch
import me.kcybulski.bricks.api.Identity
import me.kcybulski.bricks.events.CommandBus
import me.kcybulski.bricks.events.EventBus
import me.kcybulski.bricks.game.GameEndedEvent
import me.kcybulski.bricks.lobbies.LobbyAdded
import me.kcybulski.bricks.lobbies.PlayerJoinedToLobby
import me.kcybulski.bricks.lobbies.PlayerLeftLobby
import me.kcybulski.bricks.tournament.TournamentEnded
import me.kcybulski.bricks.tournament.TournamentStarted
import ratpack.websocket.WebSocket
import ratpack.websocket.WebSocketClose
import ratpack.websocket.WebSocketHandler
import ratpack.websocket.WebSocketMessage
import java.util.UUID
import java.util.concurrent.Executors.newSingleThreadExecutor

class RefreshLobbies private constructor(
    private val objectMapper: ObjectMapper = jacksonObjectMapper()
) : WebSocketHandler<String> {

    private val channel = Channel<String>(capacity = UNLIMITED)
    private val websockets: MutableList<WebSocket> = mutableListOf()


    suspend fun reportPing(pings: Map<Identity, Long>) {
        pings
            .mapKeys { (key, _) -> key.name }
            .let { ReportPing(it) }
            .let { sendToChannel(it) }
    }

    private suspend fun sendToChannel(event: Any) {
        channel.send(objectMapper.writeValueAsString(event))
    }

    override fun onOpen(webSocket: WebSocket): String {
        websockets += webSocket
        return ""
    }

    override fun onClose(close: WebSocketClose<String>) {}

    override fun onMessage(frame: WebSocketMessage<String>) {}

    companion object {

        fun configure(
            eventBus: EventBus,
            coroutine: CoroutineScope = CoroutineScope(newSingleThreadExecutor().asCoroutineDispatcher())
        ): RefreshLobbies {
            val refreshLobbies = RefreshLobbies()

            eventBus.subscribe(GameEndedEvent::class) { refreshLobbies.sendToChannel(GameEndedMessage(it.gameId)) }
            eventBus.subscribe(PlayerJoinedToLobby::class) { refreshLobbies.sendToChannel(PlayerJoinedMessage(it.player)) }
            eventBus.subscribe(PlayerLeftLobby::class) { refreshLobbies.sendToChannel(PlayerLeftMessage(it.player)) }
            eventBus.subscribe(LobbyAdded::class) { refreshLobbies.sendToChannel(LobbyAddedMessage(it.lobbyName)) }
            eventBus.subscribe(TournamentStarted::class) { refreshLobbies.sendToChannel(TournamentStartedMessage(it.tournamentId)) }
            eventBus.subscribe(TournamentEnded::class) { refreshLobbies.sendToChannel(TournamentEndedMessage(it.tournamentId)) }

            coroutine.launch {
                for (msg in refreshLobbies.channel) {
                    refreshLobbies.websockets.forEach { it.send(msg) }
                }
            }

            return refreshLobbies
        }

    }

}

private data class ReportPing(
    val players: Map<String, Long>,
    val type: String = "REPORT_PING"
)

private data class GameEndedMessage(
    val gameId: UUID,
    val type: String = "GAME_ENDED"
)

private data class PlayerJoinedMessage(
    val player: String,
    val type: String = "PLAYER_JOINED"
)

private data class PlayerLeftMessage(
    val player: String,
    val type: String = "PLAYER_LEFT"
)

private data class LobbyAddedMessage(
    val lobby: String,
    val type: String = "LOBBY_ADDED"
)

private data class TournamentStartedMessage(
    val id: UUID,
    val type: String = "TOURNAMENT_STARTED"
)

private data class TournamentEndedMessage(
    val id: UUID,
    val type: String = "TOURNAMENT_ENDED"
)
