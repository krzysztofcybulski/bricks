package me.kcybulski.bricks.tournament

import me.kcybulski.bricks.events.EventBus
import me.kcybulski.bricks.game.EndedGame
import me.kcybulski.bricks.game.GameCoordinator
import java.util.UUID

internal class DuelCoordinator(
    private val tournamentId: UUID,
    private val gameCoordinator: GameCoordinator,
    private val events: EventBus
) {

    suspend fun duel(vararg sizes: Int): DuelResult = sizes
        .flatMap { duel(it) }
        .let(::DuelResult)

    private suspend fun duel(size: Int) = listOf(
        gameCoordinator.play(gameCoordinator.players.first, size)
            .also(::sendGameEndedEvent),
        gameCoordinator.play(gameCoordinator.players.second, size)
            .also(::sendGameEndedEvent)
    )

    private fun sendGameEndedEvent(gameEnded: EndedGame) {
        events.send(GameEndedInTournament(tournamentId, gameEnded.id), tournamentId.toString())
    }
}
