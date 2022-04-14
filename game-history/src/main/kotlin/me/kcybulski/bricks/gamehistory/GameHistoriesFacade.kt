package me.kcybulski.bricks.gamehistory

import me.kcybulski.nexum.eventstore.EventStore
import java.util.UUID

class GameHistoriesFacade(
    private val eventStore: EventStore
) {

    private val gameViews = GameViews(eventStore)

    fun game(gameId: UUID): GameHistory = GameHistory(gameId, eventStore)

    fun games(tournamentId: UUID): List<GameView> = gameViews.find(tournamentId)

}
