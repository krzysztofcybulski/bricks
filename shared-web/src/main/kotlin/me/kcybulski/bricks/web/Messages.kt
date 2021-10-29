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
)
sealed interface UserMessage

class RegisterMessage(val name: String) : UserMessage
object ReadyMessage : UserMessage

@JsonTypeInfo(use = NAME, include = PROPERTY, property = "type")
@JsonSubTypes(
    JsonSubTypes.Type(value = GameStartedMessage::class, name = "start"),
    JsonSubTypes.Type(value = FirstMoveMessage::class, name = "firstmove"),
    JsonSubTypes.Type(value = MoveMessage::class, name = "move"),
    JsonSubTypes.Type(value = GameEndMessage::class, name = "end")
)
interface ServerMessage

data class GameStartedMessage(
    val id: UUID,
    val playerNames: List<String>,
    val size: Int
) : ServerMessage

class FirstMoveMessage : ServerMessage

data class MoveMessage(
    val blocks: List<PositionMessage>
) : ServerMessage, UserMessage

data class PositionMessage(val x: Int, val y: Int)

data class GameEndMessage(
    val won: Boolean
) : ServerMessage

