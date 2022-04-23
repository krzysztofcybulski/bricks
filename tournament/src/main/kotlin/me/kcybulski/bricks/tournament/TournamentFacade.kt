package me.kcybulski.bricks.tournament

import me.kcybulski.bricks.api.Algorithm
import me.kcybulski.bricks.events.EventBus
import me.kcybulski.bricks.game.GamesFactory
import me.kcybulski.bricks.tournament.RoundsPlanner.plan
import me.kcybulski.bricks.tournament.TournamentSettings.Companion.settings
import java.util.UUID

class TournamentFacade(
    private val events: EventBus
) {

    suspend fun play(
        id: UUID,
        algorithms: List<Algorithm>,
        settingsBuilder: TournamentSettingsBuilder.() -> Unit = {}
    ) = play(id, algorithms, settings(settingsBuilder))

    suspend fun play(
        id: UUID,
        algorithms: List<Algorithm>,
        settings: TournamentSettings
    ): TournamentResult = Tournament(
        id = id,
        rounds = plan(algorithms.map(Algorithm::identity)),
        algorithms = algorithms,
        settings = settings,
        gamesFactory = GamesFactory(settings.game),
        events = events
    ).playTournament()

}
