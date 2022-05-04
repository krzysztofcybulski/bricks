package me.kcybulski.bricks.server.lobby

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.UNLIMITED
import kotlinx.coroutines.launch
import me.kcybulski.bricks.api.Identity
import me.kcybulski.bricks.game.GameEndedEvent
import me.kcybulski.bricks.lobbies.LobbyAdded
import me.kcybulski.bricks.lobbies.PlayerJoinedToLobby
import me.kcybulski.bricks.lobbies.PlayerLeftLobby
import me.kcybulski.bricks.tournament.TournamentEnded
import me.kcybulski.bricks.tournament.TournamentStarted
import me.kcybulski.nexum.eventstore.EventStore
import ratpack.websocket.WebSocket
import ratpack.websocket.WebSocketClose
import ratpack.websocket.WebSocketHandler
import ratpack.websocket.WebSocketMessage
import java.util.UUID

class RefreshLobbies(
    eventStore: EventStore,
    coroutine: CoroutineScope,
    private val objectMapper: ObjectMapper = jacksonObjectMapper()
) : WebSocketHandler<String> {

    private val channel = Channel<String>(capacity = UNLIMITED)

    private val websockets: MutableList<WebSocket> = mutableListOf()

    init {
        eventStore.subscribe(GameEndedEvent::class) { sendToChannel(GameEndedMessage(it.gameId)) }
        eventStore.subscribe(PlayerJoinedToLobby::class) { sendToChannel(PlayerJoinedMessage(it.player)) }
        eventStore.subscribe(PlayerLeftLobby::class) { sendToChannel(PlayerLeftMessage(it.player)) }
        eventStore.subscribe(LobbyAdded::class) { sendToChannel(LobbyAddedMessage(it.lobbyName)) }
        eventStore.subscribe(TournamentStarted::class) { sendToChannel(TournamentStartedMessage(it.tournamentId)) }
        eventStore.subscribe(TournamentEnded::class) { sendToChannel(TournamentEndedMessage(it.tournamentId)) }

        coroutine.launch {
            for (msg in channel) {
                websockets.forEach { it.send(msg) }
            }
        }
    }

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
