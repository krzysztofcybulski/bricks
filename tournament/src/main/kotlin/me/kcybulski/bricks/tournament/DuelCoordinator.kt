package me.kcybulski.bricks.tournament

import me.kcybulski.bricks.events.EventBus
import me.kcybulski.bricks.game.EndedGame
import me.kcybulski.bricks.game.GameCoordinator
import me.kcybulski.bricks.game.GameEvent
import me.kcybulski.bricks.game.Identity
import java.util.UUID

internal class DuelCoordinator(
    private val gameCoordinator: GameCoordinator,
    private val events: EventBus
) {

    suspend fun duel(vararg sizes: Int): DuelResult = sizes
        .flatMap { duel(it) }
        .let(::DuelResult)

    private suspend fun duel(size: Int) = listOf(
        playGame(gameCoordinator.players.first, size),
        playGame(gameCoordinator.players.second, size)
    )

    private suspend fun playGame(firstPlayer: Identity, size: Int) = gameCoordinator
        .play(firstPlayer, size)
        .also { events.send(GameEndedEvent(it.id, it), it.id.toString()) }

}

private class GameEndedEvent(
    override val gameId: UUID,
    val game: EndedGame
) : GameEvent
