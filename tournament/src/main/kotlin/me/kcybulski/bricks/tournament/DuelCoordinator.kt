package me.kcybulski.bricks.tournament

import me.kcybulski.bricks.events.EventBus
import me.kcybulski.bricks.game.EndedGame
import me.kcybulski.bricks.game.GameCoordinator
import me.kcybulski.bricks.game.GameEvent
import java.util.UUID

internal class DuelCoordinator(
    private val gameCoordinator: GameCoordinator,
    private val events: EventBus
) {

    suspend fun duel(vararg sizes: Int): DuelResult = sizes
        .flatMap { duel(it) }
        .let(::DuelResult)

    private suspend fun duel(size: Int) = listOf(
        gameCoordinator.play(gameCoordinator.players.first, size).also { events.send(GameEndedEvent(it.id, it)) },
        gameCoordinator.play(gameCoordinator.players.second, size).also { events.send(GameEndedEvent(it.id, it)) }
    )
}

private class GameEndedEvent(
    override val gameId: UUID,
    val game: EndedGame
): GameEvent
