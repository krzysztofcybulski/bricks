package me.kcybulski.bricks.gamehistory

import me.kcybulski.bricks.game.GameEndedEvent
import me.kcybulski.bricks.game.GameStartedEvent
import me.kcybulski.bricks.game.GameWonResult
import me.kcybulski.bricks.game.TieResult
import me.kcybulski.bricks.gamehistory.GameState.ENDED
import me.kcybulski.bricks.gamehistory.GameState.IN_PROGRESS
import me.kcybulski.bricks.tournament.GameEndedInTournament
import me.kcybulski.nexum.eventstore.EventStore
import java.util.UUID

internal class GameViews(eventStore: EventStore) {

    private val games: MutableMap<UUID, GameView> = mutableMapOf()
    private val tournamentsAndGames: MutableMap<UUID, List<UUID>> = mutableMapOf()

    init {
        eventStore.subscribe(GameStartedEvent::class, ::onGameStarted)
        eventStore.subscribe(GameEndedEvent::class, ::onGameEnded)
        eventStore.subscribe(GameEndedInTournament::class, ::onGameEndedInTournament)
    }

    fun find(tournamentId: UUID): List<GameView> = tournamentsAndGames[tournamentId]
        ?.let { gameIds -> gameIds.mapNotNull { games[it] } }
        ?: emptyList()

    private fun onGameStarted(event: GameStartedEvent) {
        games[event.gameId] = GameView(
            id = event.gameId,
            players = event.players.first.name to event.players.second.name,
            size = event.size,
            state = IN_PROGRESS,
            winner = null
        )
    }

    private fun onGameEnded(event: GameEndedEvent) {
        games[event.gameId]?.let { gameView ->
            gameView.state = ENDED
            gameView.winner = when (event.result) {
                is GameWonResult -> (event.result as GameWonResult).player.name
                TieResult -> null
            }
        }
    }

    private fun onGameEndedInTournament(event: GameEndedInTournament) {
        tournamentsAndGames.merge(event.tournamentId, listOf(event.gameId)) { old, new -> old + new }
    }
}

data class GameView(
    val id: UUID,
    var size: Int,
    var players: Pair<String, String>,
    var state: GameState,
    var winner: String?
)

enum class GameState {
    IN_PROGRESS, ENDED
}
