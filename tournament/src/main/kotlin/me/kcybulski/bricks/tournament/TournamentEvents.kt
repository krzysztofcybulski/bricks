package me.kcybulski.bricks.tournament

import java.util.UUID

interface TournamentEvent {
    val tournamentId: UUID
}

data class TournamentStarted(
    override val tournamentId: UUID
): TournamentEvent

data class GameEndedInTournament(
    override val tournamentId: UUID,
    val gameId: UUID
): TournamentEvent

data class TournamentEnded(
    override val tournamentId: UUID
): TournamentEvent
