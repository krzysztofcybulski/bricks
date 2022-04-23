package me.kcybulski.bricks.tournament

import me.kcybulski.bricks.api.Identity
import me.kcybulski.bricks.api.PlayersPair

internal object RoundsPlanner {

    fun plan(algorithms: List<Identity>): List<Round> = duelsToRounds(allDuels(algorithms), emptyList())

    private fun duelsToRounds(duels: List<PlayersPair>, rounds: List<Round>): List<Round> {
        if (duels.isEmpty()) {
            return rounds
        }
        val round = randomRound(duels)
        return duelsToRounds(duels - round.duels.toSet(), rounds + round)
    }

    private fun randomRound(allDuels: List<PlayersPair>, round: Round = Round(emptyList())): Round {
        val available = allDuels.filterNot { round.containsAnyPlayer(it) }
        if (available.isEmpty()) {
            return round
        }
        return randomRound(allDuels, Round(round.duels + available.random()))
    }

    private fun allDuels(algorithms: List<Identity>) = algorithms
        .flatMapIndexed { i, alg -> algorithms.drop(i + 1).map { PlayersPair(it, alg) } }
        .filterNot { it.first == it.second }

}

internal class Round(
    val duels: List<PlayersPair>
) {

    fun containsAnyPlayer(algorithms: PlayersPair) = duels.any {
        algorithms.first in it || algorithms.second in it
    }

}
