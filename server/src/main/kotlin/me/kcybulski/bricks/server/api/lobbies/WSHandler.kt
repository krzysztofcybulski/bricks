package me.kcybulski.bricks.server.api.lobbies

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.kcybulski.bricks.auth.ApiUser
import me.kcybulski.bricks.events.CommandBus
import me.kcybulski.bricks.lobbies.JoinLobbyCommand
import me.kcybulski.bricks.lobbies.LobbyId
import me.kcybulski.bricks.server.PlayerConnection
import me.kcybulski.bricks.web.ImHealthy
import me.kcybulski.bricks.web.MoveMessage
import me.kcybulski.bricks.web.ReadyMessage
import me.kcybulski.bricks.web.UserMessage
import mu.KotlinLogging
import ratpack.websocket.WebSocket
import ratpack.websocket.WebSocketClose
import ratpack.websocket.WebSocketHandler
import ratpack.websocket.WebSocketMessage

class WSHandler(
    private val lobbyId: LobbyId,
    private val registry: WebsocketsRegistry,
    private val apiUser: ApiUser,
    private val coroutine: CoroutineScope,
    private val commandBus: CommandBus,
    private val objectMapper: ObjectMapper = jacksonObjectMapper()
) : WebSocketHandler<String> {

    private val logger = KotlinLogging.logger {}

    override fun onOpen(webSocket: WebSocket): String? {
        val playerConnection = PlayerConnection(apiUser.id, lobbyId, webSocket)
        registry.register(playerConnection)
        commandBus.send(JoinLobbyCommand(lobbyId, playerConnection))
        return null
    }

    override fun onClose(close: WebSocketClose<String>) {}

    override fun onMessage(frame: WebSocketMessage<String>) {
        coroutine.launch {
            parseUserMessage(frame)
                .onSuccess { handleMessage(it, frame.connection) }
                .onFailure { logger.warn(it) { "Ignoring user message ${frame.text}" } }
        }
    }

    private suspend fun parseUserMessage(frame: WebSocketMessage<String>) = withContext(IO) {
        runCatching {
            objectMapper.readValue(frame.text, UserMessage::class.java)
        }
    }

    private suspend fun handleMessage(message: UserMessage, connection: WebSocket) = coroutineScope {
        when (message) {
            is ReadyMessage -> registry.find(connection)?.ready()
            is MoveMessage -> registry.find(connection)?.moved(message)
            is ImHealthy -> registry.find(connection)?.healthy()
            else -> {}
        }
    }
}
