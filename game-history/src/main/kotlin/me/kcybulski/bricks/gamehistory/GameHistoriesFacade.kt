package me.kcybulski.bricks.gamehistory

import me.kcybulski.nexum.eventstore.EventStore
import java.util.UUID

class GameHistoriesFacade(
    private val eventStore: EventStore
) {

    fun game(gameId: UUID): GameHistory = GameHistory(gameId, eventStore)

}
