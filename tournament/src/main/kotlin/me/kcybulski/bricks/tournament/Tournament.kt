package me.kcybulski.bricks.tournament

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import me.kcybulski.bricks.events.EventBus
import me.kcybulski.bricks.game.Algorithm
import me.kcybulski.bricks.game.AlgorithmsPair
import me.kcybulski.bricks.game.GameCoordinator
import me.kcybulski.bricks.game.GamesFactory
import me.kcybulski.bricks.game.Identity
import me.kcybulski.bricks.game.PlayersPair

internal class Tournament(
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
        awaitAll(
            *round.duels.map { async { playDuel(it) } }.toTypedArray()
        )
            .flatMap { it.games }
            .let { RoundResult(it) }
    }

    private suspend fun playDuel(duel: PlayersPair): DuelResult =
        GameCoordinator(duel.algorithmsPair(algorithms), settings.game, gamesFactory, events)
            .let { DuelCoordinator(it, events) }
            .duel(*settings.mapSizesPerDuel.toIntArray())

}

private fun PlayersPair.algorithmsPair(algorithms: List<Algorithm>) =
    AlgorithmsPair(algorithms[first], algorithms[second])

private operator fun List<Algorithm>.get(identity: Identity) =
    find { it.identity == identity } ?: throw IllegalArgumentException("No $identity player found")
