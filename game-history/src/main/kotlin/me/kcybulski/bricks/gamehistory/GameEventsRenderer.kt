package me.kcybulski.bricks.gamehistory

import me.kcybulski.bricks.game.GameEvent

object GameEventsRenderer {

    fun toEventResponse(gameEvent: GameEvent) =
        GameEventResponse(
            type = gameEvent.javaClass.simpleName,
            gameId = gameEvent.gameId.toString(),
            rawPayload = gameEvent
        )

}

data class GameEventResponse(
    val type: String,
    val gameId: String,
    val rawPayload: Any
)
