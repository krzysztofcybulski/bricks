package me.kcybulski.bricks.server.views.lobbies

import me.kcybulski.bricks.events.EventBus
import me.kcybulski.bricks.lobbies.LobbyAdded
import me.kcybulski.bricks.lobbies.LobbyClosed
import me.kcybulski.bricks.lobbies.LobbyDeleted
import me.kcybulski.bricks.lobbies.LobbyId
import me.kcybulski.bricks.lobbies.LobbyStartedTournament
import me.kcybulski.bricks.lobbies.PlayerJoinedToLobby
import me.kcybulski.bricks.lobbies.PlayerLeftLobby
import me.kcybulski.bricks.server.views.lobbies.LobbyView.Player
import me.kcybulski.bricks.server.views.lobbies.LobbyView.Status.CLOSED
import me.kcybulski.bricks.server.views.lobbies.LobbyView.Status.IN_GAME
import me.kcybulski.bricks.server.views.lobbies.LobbyView.Status.OPEN
import java.util.UUID

class LobbiesListReadModel private constructor() {

    private val memory: MutableMap<String, LobbyView> = mutableMapOf()

    fun findAllLobbies(): List<LobbyView> =
        memory.values.toList()

    fun findLobby(id: LobbyId): LobbyView? =
        memory[id.raw.toString()]

    fun findLobby(name: String): LobbyView? =
        memory.values.find { it.name == name }

    private fun onLobbyAdded(event: LobbyAdded) {
        memory[event.lobbyId.raw.toString()] = LobbyView(
            id = event.lobbyId.raw,
            name = event.lobbyName,
            image = "https://avatars.dicebear.com/api/bottts/${event.lobbyId.raw}.svg?style=circle",
            players = emptyList(),
            status = OPEN
        )
    }

    private fun onLobbyDeleted(event: LobbyDeleted) {
        memory.remove(event.lobbyId.raw.toString())
    }

    private fun onPlayerJoinedToLobby(event: PlayerJoinedToLobby) {
        val player = Player(
            event.player,
            "https://avatars.dicebear.com/api/avataaars/${event.player}.svg?style=circle"
        )
        memory[event.lobbyId.raw.toString()]
            ?.let { it.copy(players = it.players + player) }
            ?.let { memory[event.lobbyId.raw.toString()] = it }
    }

    private fun onPlayerLeftLobby(event: PlayerLeftLobby) {
        memory[event.lobbyId.raw.toString()]
            ?.let { it.copy(players = it.players.filter { p -> p.name != event.player }) }
            ?.let { memory[event.lobbyId.raw.toString()] = it }
    }

    private fun onLobbyStartedTournament(event: LobbyStartedTournament) {
        memory[event.lobbyId.raw.toString()]
            ?.copy(status = IN_GAME)
            ?.let { memory[event.lobbyId.raw.toString()] = it }
    }

    private fun onLobbyClosed(event: LobbyClosed) {
        memory[event.lobbyId.raw.toString()]
            ?.copy(status = CLOSED)
            ?.let { memory[event.lobbyId.raw.toString()] = it }
    }

    companion object {

        fun configureInMemory(eventBus: EventBus): LobbiesListReadModel {
            val lobbiesViews = LobbiesListReadModel()

            eventBus.subscribe(LobbyAdded::class, lobbiesViews::onLobbyAdded)
            eventBus.subscribe(LobbyDeleted::class, lobbiesViews::onLobbyDeleted)
            eventBus.subscribe(PlayerJoinedToLobby::class, lobbiesViews::onPlayerJoinedToLobby)
            eventBus.subscribe(PlayerLeftLobby::class, lobbiesViews::onPlayerLeftLobby)
            eventBus.subscribe(LobbyStartedTournament::class, lobbiesViews::onLobbyStartedTournament)
            eventBus.subscribe(LobbyClosed::class, lobbiesViews::onLobbyClosed)

            return lobbiesViews
        }

    }

}

data class LobbyView(
    val id: UUID,
    val name: String,
    val image: String,
    val players: List<Player>,
    val status: Status
) {

    enum class Status {

        OPEN, IN_GAME, CLOSED

    }

    data class Player(
        val name: String,
        val avatarUrl: String
    )

}
