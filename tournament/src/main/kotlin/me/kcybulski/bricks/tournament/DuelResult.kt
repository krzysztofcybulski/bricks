package me.kcybulski.bricks.tournament

import me.kcybulski.bricks.game.EndedGame

data class DuelResult(
    val games: List<EndedGame>
)

data class RoundResult(
    val games: List<EndedGame>
)
data class TournamentResult(
    val games: List<EndedGame>
)
