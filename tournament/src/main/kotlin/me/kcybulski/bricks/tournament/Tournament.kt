package me.kcybulski.bricks.tournament

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import me.kcybulski.bricks.api.Algorithm
import me.kcybulski.bricks.api.Identity
import me.kcybulski.bricks.api.PlayersPair
import me.kcybulski.bricks.events.EventBus
import me.kcybulski.bricks.game.AlgorithmsPair
import me.kcybulski.bricks.game.GameCoordinator
import me.kcybulski.bricks.game.GamesFactory
import java.util.UUID

internal class Tournament(
    val id: UUID,
    private val rounds: List<Round>,
    private val algorithms: List<Algorithm>,
    private val settings: TournamentSettings,
    private val gamesFactory: GamesFactory,
    private val events: EventBus
) {

    suspend fun playTournament(): TournamentResult = TournamentResult(
        rounds.flatMap { playRound(it).games }
    )

    private suspend fun playRound(round: Round): RoundResult = coroutineScope {
        round.duels.map { async { playDuel(it) } }
            .awaitAll()
            .flatMap(DuelResult::games)
            .let(::RoundResult)
    }

    private suspend fun playDuel(duel: PlayersPair): DuelResult =
        GameCoordinator(duel.algorithmsPair(algorithms), settings.game, gamesFactory, events)
            .let { DuelCoordinator(id, it, events) }
            .duel(*settings.mapSizesPerDuel.toIntArray())

}

private fun PlayersPair.algorithmsPair(algorithms: List<Algorithm>) =
    AlgorithmsPair(algorithms[first], algorithms[second])

private operator fun List<Algorithm>.get(identity: Identity) =
    find { it.identity == identity } ?: throw IllegalArgumentException("No $identity player found")
