package me.kcybulski.bricks.tournament

import me.kcybulski.bricks.events.EventBus
import me.kcybulski.bricks.game.Algorithm
import me.kcybulski.bricks.tournament.RoundsPlanner.plan
import me.kcybulski.bricks.tournament.TournamentSettings.Companion.settings

class TournamentFacade(
    private val events: EventBus
) {

    suspend fun play(
        algorithms: List<Algorithm>,
        settingsBuilder: TournamentSettingsBuilder.() -> Unit = {}
    ) = play(algorithms, settings(settingsBuilder))

    suspend fun play(
        algorithms: List<Algorithm>,
        settings: TournamentSettings
    ): TournamentResult = Tournament(
        rounds = plan(algorithms.map(Algorithm::identity)),
        algorithms = algorithms,
        settings = settings,
        events = events
    ).playTournament()

}
