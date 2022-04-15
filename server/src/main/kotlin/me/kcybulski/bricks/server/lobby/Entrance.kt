package me.kcybulski.bricks.server.lobby

import me.kcybulski.bricks.tournament.TournamentFacade
import me.kcybulski.bricks.tournament.TournamentSettings

class Entrance private constructor(
    private val lobbyFactory: LobbyFactory
) {

    private val lobbies: MutableMap<String, Lobby> = mutableMapOf()

    fun lobbies(): List<Lobby> = lobbies.values.toList()

    operator fun get(name: String) = lobbies[name]

    fun newLobby(): Lobby = lobbyFactory.create().also {
        lobbies[it.name] = it
    }

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

    companion object {

        fun createWithLobbies(lobbiesAmount: Int, lobbyFactory: LobbyFactory = LobbyFactory()): Entrance {
            val entrance = Entrance(lobbyFactory)
            repeat(lobbiesAmount) {
                entrance.newLobby()
            }
            return entrance
        }
    }
}
