package me.kcybulski.bricks.server.lobby

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.launch
import me.kcybulski.bricks.tournament.TournamentFacade
import me.kcybulski.bricks.tournament.TournamentSettings
import java.util.concurrent.Executors

class Entrance(
    private val lobbyFactory: LobbyFactory
) {

    private val lobbiesScope = CoroutineScope(
        Executors.newSingleThreadExecutor()
            .asCoroutineDispatcher()
    )

    private val lobbies: MutableMap<String, Lobby> = mutableMapOf()

    fun lobbies(): List<Lobby> = lobbies.values.toList()

    operator fun get(name: String) = lobbies[name]

    fun newLobby(): Lobby = lobbyFactory.create().also {
        lobbies[it.name] = it
    }

    fun start(name: String, tournaments: TournamentFacade, settings: TournamentSettings): InGameLobby? {
        val lobby = lobbies[name]
        if (lobby !is OpenLobby) {
            return null
        }
        val inProgress = lobby.inProgress(tournaments, settings)
            .also { lobbies[name] = it }
        lobbiesScope.launch {
            inProgress
                .run()
                .also { lobbies[name] = it }
        }
        return inProgress
    }
}
