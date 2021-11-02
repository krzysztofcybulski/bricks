package me.kcybulski.bricks.server

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.kotest.common.runBlocking
import kotlinx.coroutines.coroutineScope
import me.kcybulski.bricks.web.MoveMessage
import me.kcybulski.bricks.web.ReadyMessage
import me.kcybulski.bricks.web.RegisterMessage
import me.kcybulski.bricks.web.UserMessage
import mu.KotlinLogging
import ratpack.websocket.WebSocket
import ratpack.websocket.WebSocketClose
import ratpack.websocket.WebSocketHandler
import ratpack.websocket.WebSocketMessage

class WSHandler(
    private val lobby: OpenLobby,
    private val objectMapper: ObjectMapper = jacksonObjectMapper()
) : WebSocketHandler<String> {

    private val logger = KotlinLogging.logger {}

    override fun onOpen(webSocket: WebSocket): String? = null

    override fun onClose(close: WebSocketClose<String>) {}

    override fun onMessage(frame: WebSocketMessage<String>) {
        runBlocking { handleMessage(objectMapper.readValue(frame.text, UserMessage::class.java), frame.connection) }
    }

    private suspend fun handleMessage(message: UserMessage, connection: WebSocket) = coroutineScope {
        when (message) {
            is RegisterMessage -> lobby.registerPlayer(message.name, connection)
            is ReadyMessage -> lobby.ready(connection)
            is MoveMessage -> lobby.moved(connection, message)
        }
    }
}
