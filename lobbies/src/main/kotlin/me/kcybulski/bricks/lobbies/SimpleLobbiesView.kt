package me.kcybulski.bricks.lobbies

import me.kcybulski.bricks.events.EventBus
import me.kcybulski.bricks.lobbies.SimpleLobbyStatus.CLOSED
import me.kcybulski.bricks.lobbies.SimpleLobbyStatus.IN_GAME
import me.kcybulski.bricks.lobbies.SimpleLobbyStatus.OPEN
import java.util.UUID

class SimpleLobbiesView private constructor() {

    private val memory: MutableMap<String, SimpleLobby> = mutableMapOf()

    fun findAllLobbies(): List<SimpleLobby> =
        memory.values.toList()

    fun findLobby(id: LobbyId): SimpleLobby? =
        memory[id.raw.toString()]

    fun findLobby(name: String): SimpleLobby? =
        memory.values.find { it.name == name }

    private fun onLobbyAdded(event: LobbyAdded) {
        memory[event.lobbyId.raw.toString()] = SimpleLobby(
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
        val player = SimplePlayerInLobby(event.player, "https://avatars.dicebear.com/api/avataaars/${event.player}.svg?style=circle")
        memory[event.lobbyId.raw.toString()]
            ?.let { it.copy(players = it.players + player) }
            ?.let { memory[event.lobbyId.raw.toString()] = it }
    }

    private fun onPlayerLeftLobby(event: PlayerLeftLobby) {
        memory[event.lobbyId.raw.toString()]
            ?.let { it.copy(players = it.players.filter { p -> p.name != event.player } ) }
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

        fun inMemory(eventBus: EventBus): SimpleLobbiesView {
            val lobbiesViews = SimpleLobbiesView()

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

data class SimpleLobby(
    val id: UUID,
    val name: String,
    val image: String,
    val players: List<SimplePlayerInLobby>,
    val status: SimpleLobbyStatus
)

enum class SimpleLobbyStatus {

    OPEN, IN_GAME, CLOSED

}

data class SimplePlayerInLobby(
    val name: String,
    val avatarUrl: String
)

