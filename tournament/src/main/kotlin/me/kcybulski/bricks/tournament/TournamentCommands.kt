package me.kcybulski.bricks.tournament

import me.kcybulski.bricks.api.Algorithm
import java.util.UUID

data class StartNewTournament(
    val id: UUID,
    val players: List<Algorithm>,
    val settings: TournamentSettings
)
