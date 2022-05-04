package me.kcybulski.bricks.server

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.UNLIMITED
import kotlinx.coroutines.withTimeoutOrNull
import me.kcybulski.bricks.api.Algorithm
import me.kcybulski.bricks.api.Block
import me.kcybulski.bricks.api.Brick
import me.kcybulski.bricks.api.DuoBrick
import me.kcybulski.bricks.api.GameInitialized
import me.kcybulski.bricks.api.Identity
import me.kcybulski.bricks.api.MoveTrigger
import me.kcybulski.bricks.lobbies.LobbyId
import me.kcybulski.bricks.web.FirstMoveMessage
import me.kcybulski.bricks.web.GameStartedMessage
import me.kcybulski.bricks.web.HowAreYou
import me.kcybulski.bricks.web.MoveMessage
import me.kcybulski.bricks.web.PositionMessage
import me.kcybulski.bricks.web.ReadyMessage
import me.kcybulski.bricks.web.ServerMessage
import me.kcybulski.bricks.web.UserMessage
import ratpack.websocket.WebSocket
import java.lang.System.currentTimeMillis

class PlayerConnection(
    name: String,
    val lobbyId: LobbyId,
    val webSocket: WebSocket,
    private val objectMapper: ObjectMapper = jacksonObjectMapper()
) : Algorithm {

    private val healthChannel = Channel<Boolean>(UNLIMITED)
    private val channel = Channel<UserMessage>(UNLIMITED)

    override val identity: Identity = Identity(name)

    suspend fun ready() {
        channel.send(ReadyMessage)
    }

    suspend fun moved(message: MoveMessage) {
        channel.send(message)
    }

    suspend fun healthy() {
        healthChannel.send(true)
    }

    override suspend fun initialize(gameInitialized: GameInitialized) {
        send(
            GameStartedMessage(
                id = gameInitialized.gameId,
                playerNames = listOf(gameInitialized.players.first.name, gameInitialized.players.second.name),
                size = gameInitialized.size,
                blocks = gameInitialized.initialBlocks.map { PositionMessage(it.x, it.y) }.toSet()
            )
        )
        while (channel.receive() !is ReadyMessage) {
        }
    }

    override suspend fun move(last: MoveTrigger): Brick {
        when (last) {
            is MoveTrigger.FirstMove -> send(FirstMoveMessage())
            is MoveTrigger.OpponentMoved -> send(MoveMessage(last.brick.blocks.map { PositionMessage(it.x, it.y) }))
        }
        return (channel.receive() as MoveMessage).toBrick()
    }

    suspend fun healthStatus(): HealthStatus {
        send(HowAreYou)
        val time = currentTimeMillis()
        return withTimeoutOrNull(1000) { healthChannel.receive() }
            ?.let { Healthy(currentTimeMillis() - time) }
            ?: NotHealthy
    }

    private fun send(message: ServerMessage) {
        webSocket.send(objectMapper.writeValueAsString(message))
    }

}

private fun MoveMessage.toBrick() = DuoBrick.unsafe(
    Block(blocks[0].x, blocks[0].y),
    Block(blocks[1].x, blocks[1].y)
)

sealed class HealthStatus

object NotHealthy : HealthStatus()

class Healthy(val answerInMillis: Long) : HealthStatus()
