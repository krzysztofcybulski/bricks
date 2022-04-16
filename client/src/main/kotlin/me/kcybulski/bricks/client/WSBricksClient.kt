package me.kcybulski.bricks.client

import com.fasterxml.jackson.databind.ObjectMapper
import io.ktor.client.HttpClient
import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.http.HttpMethod.Companion.Get
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.withContext
import me.kcybulski.bricks.game.Algorithm
import me.kcybulski.bricks.game.Block
import me.kcybulski.bricks.game.Brick
import me.kcybulski.bricks.game.DuoBrick
import me.kcybulski.bricks.game.GameInitialized
import me.kcybulski.bricks.game.Identity
import me.kcybulski.bricks.game.MoveTrigger
import me.kcybulski.bricks.game.PlayersPair
import me.kcybulski.bricks.web.FirstMoveMessage
import me.kcybulski.bricks.web.GameStartedMessage
import me.kcybulski.bricks.web.HowAreYou
import me.kcybulski.bricks.web.ImHealthy
import me.kcybulski.bricks.web.MoveMessage
import me.kcybulski.bricks.web.PositionMessage
import me.kcybulski.bricks.web.ReadyMessage
import me.kcybulski.bricks.web.RegisterMessage
import me.kcybulski.bricks.web.ServerMessage
import mu.KotlinLogging

internal class WSBricksClient(
    private val http: HttpClient,
    private val host: String,
    private val port: Int,
    private val jackson: ObjectMapper
) {

    private val logger = KotlinLogging.logger {}

    suspend fun connect(lobby: String, bricks: Algorithm) = withContext(SupervisorJob()) {
        http.webSocket(method = Get, host = host, port = port, path = "/${lobby}/game") {
            sendJson(RegisterMessage(bricks.identity.name))
            logger.info { "Registered as ${bricks.identity.name}" }
            incoming.consumeAsFlow()
                .mapNotNull { it as? Frame.Text }
                .map { it.fromJson<ServerMessage>() }
                .collect {
                    when (it) {
                        is GameStartedMessage -> {
                            logger.info {
                                "Game ${it.id} started vs ${
                                    it.playerNames.filterNot { name -> name == bricks.identity.name }.first()
                                }."
                            }
                            bricks.initialize(
                                GameInitialized(
                                    it.id,
                                    it.size,
                                    PlayersPair(
                                        Identity(it.playerNames[0]),
                                        Identity(it.playerNames[1])
                                    ),
                                    it.blocks.map(::toMove).toSet()
                                )
                            )
                            sendJson(ReadyMessage)
                        }
                        is FirstMoveMessage, is MoveMessage -> {
                            when (it) {
                                is FirstMoveMessage -> logger.info { "Waiting for your first move" }
                                is MoveMessage -> logger.info { "Opponent placed ${it.blocks.joinToString(",") { "${it.x}x${it.y}" }}" }
                            }
                            val move = bricks.move(toMove(it))
                            logger.info { "Placed brick on ${move.blocks.joinToString(",") { "${it.x}x${it.y}" }}" }
                            sendJson(from(move))
                        }
                        is HowAreYou -> sendJson(ImHealthy)
                    }
                }
        }
    }

    fun disconnect() {
        http.close()
    }

    private fun toMove(message: ServerMessage): MoveTrigger = when (message) {
        is MoveMessage -> MoveTrigger.OpponentMoved(
            DuoBrick.unsafe(
                Block(message.blocks[0].x, message.blocks[0].y),
                Block(message.blocks[1].x, message.blocks[1].y)
            )
        )
        is FirstMoveMessage -> MoveTrigger.FirstMove
        else -> throw IllegalStateException("Unknown move!")
    }

    private fun toMove(message: PositionMessage) = Block(message.x, message.y)

    private fun from(move: Brick) = MoveMessage(move.blocks.map { PositionMessage(it.x, it.y) })

    private suspend fun <T> DefaultClientWebSocketSession.sendJson(t: T) =
        send(Frame.Text(jackson.writeValueAsString(t)))

    private inline fun <reified T> Frame.Text.fromJson() = jackson.readValue(this.readText(), T::class.java)

}
