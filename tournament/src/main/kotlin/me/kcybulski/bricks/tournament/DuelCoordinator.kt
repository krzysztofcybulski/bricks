package me.kcybulski.bricks.tournament

import me.kcybulski.bricks.events.EventBus
import me.kcybulski.bricks.game.EndedGame
import me.kcybulski.bricks.game.GameCoordinator

internal class DuelCoordinator(
    private val gameCoordinator: GameCoordinator,
    private val events: EventBus
) {

    suspend fun duel(vararg sizes: Int): DuelResult = sizes
        .flatMap { duel(it) }
        .let(::DuelResult)

    private suspend fun duel(size: Int) = listOf(
        gameCoordinator.play(gameCoordinator.players.first, size).also { events.send(GameEndedEvent(it)) },
        gameCoordinator.play(gameCoordinator.players.second, size).also { events.send(GameEndedEvent(it)) }
    )
}

private class GameEndedEvent(
    val game: EndedGame
)
