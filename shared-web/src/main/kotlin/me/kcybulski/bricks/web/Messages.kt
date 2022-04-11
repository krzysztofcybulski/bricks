package me.kcybulski.bricks.web

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.annotation.JsonTypeInfo.As.PROPERTY
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME
import java.util.UUID


@JsonTypeInfo(use = NAME, include = PROPERTY, property = "type")
@JsonSubTypes(
    JsonSubTypes.Type(value = RegisterMessage::class, name = "register"),
    JsonSubTypes.Type(value = ReadyMessage::class, name = "ready"),
    JsonSubTypes.Type(value = MoveMessage::class, name = "move"),
    JsonSubTypes.Type(value = ImHealthy::class, name = "healthy")
)
sealed interface UserMessage

class RegisterMessage(val name: String) : UserMessage
object ReadyMessage : UserMessage
object ImHealthy : UserMessage

@JsonTypeInfo(use = NAME, include = PROPERTY, property = "type")
@JsonSubTypes(
    JsonSubTypes.Type(value = GameStartedMessage::class, name = "start"),
    JsonSubTypes.Type(value = FirstMoveMessage::class, name = "firstmove"),
    JsonSubTypes.Type(value = MoveMessage::class, name = "move"),
    JsonSubTypes.Type(value = GameEndMessage::class, name = "end"),
    JsonSubTypes.Type(value = HowAreYou::class, name = "healthcheck")
)
interface ServerMessage

data class GameStartedMessage(
    val id: UUID,
    val playerNames: List<String>,
    val size: Int,
    val blocks: Set<PositionMessage>
) : ServerMessage

class FirstMoveMessage : ServerMessage

data class MoveMessage(
    val blocks: List<PositionMessage>
) : ServerMessage, UserMessage

data class PositionMessage(val x: Int, val y: Int)

data class GameEndMessage(
    val won: Boolean
) : ServerMessage

object HowAreYou : ServerMessage
