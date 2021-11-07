package me.kcybulski.bricks.server

import arrow.core.getOrHandle
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.withTimeoutOrNull
import me.kcybulski.bricks.game.Algorithm
import me.kcybulski.bricks.game.Block
import me.kcybulski.bricks.game.Brick
import me.kcybulski.bricks.game.DuoBrick
import me.kcybulski.bricks.game.Identity
import me.kcybulski.bricks.game.MoveTrigger
import me.kcybulski.bricks.game.NewGame
import me.kcybulski.bricks.web.FirstMoveMessage
import me.kcybulski.bricks.web.GameStartedMessage
import me.kcybulski.bricks.web.HowAreYou
import me.kcybulski.bricks.web.MoveMessage
import me.kcybulski.bricks.web.PositionMessage
import me.kcybulski.bricks.web.ReadyMessage
import me.kcybulski.bricks.web.ServerMessage
import me.kcybulski.bricks.web.UserMessage
import ratpack.websocket.WebSocket

class PlayerConnection(
    val name: String,
    val webSocket: WebSocket,
    private val objectMapper: ObjectMapper = jacksonObjectMapper()
) : Algorithm {

    val healthChannel = Channel<Boolean>()
    val channel = Channel<UserMessage>()

    override val identity: Identity = Identity(name)

    override suspend fun initialize(game: NewGame) {
        send(GameStartedMessage(game.id, listOf(game.players.first.name, game.players.second.name), game.size))
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

    suspend fun isHealthy(): Boolean {
        send(HowAreYou)
        return withTimeoutOrNull(500) { healthChannel.receive() } ?: false
    }

    private fun send(message: ServerMessage) {
        webSocket.send(objectMapper.writeValueAsString(message))
    }

}

private fun MoveMessage.toBrick() = DuoBrick.of(
    Block(blocks[0].x, blocks[0].y),
    Block(blocks[1].x, blocks[1].y)
).getOrHandle { throw IllegalStateException() }
