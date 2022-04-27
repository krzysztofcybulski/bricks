package me.kcybulski.bricks.server.lobby

import me.kcybulski.bricks.events.EventBus
import me.kcybulski.bricks.tournament.TournamentFacade
import me.kcybulski.bricks.tournament.TournamentSettings

class Entrance(
    private val lobbyFactory: LobbyFactory,
    private val eventBus: EventBus
) {

    private val lobbies: MutableMap<String, Lobby> = mutableMapOf()

    fun lobbies(): List<Lobby> = lobbies.values.toList()

    operator fun get(name: String) = lobbies[name]

    fun newLobby(name: String? = null): Lobby =
        lobbyFactory.create(name)
            .also { lobbies[it.name] = it }
            .also { eventBus.send(LobbyAdded(it.name, it.id)) }

    suspend fun start(name: String, tournaments: TournamentFacade, settings: TournamentSettings): InGameLobby? {
        val lobby = lobbies[name]
        if (lobby !is OpenLobby) {
            return null
        }
        val inProgress = lobby.inProgress(tournaments, settings)
            .also { lobbies[name] = it }
        inProgress
            .run()
            .also { lobbies[name] = it }
        return inProgress
    }
}
