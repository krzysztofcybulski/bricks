package me.kcybulski.bricks.server.views.lobbies

import me.kcybulski.bricks.events.EventBus
import me.kcybulski.bricks.lobbies.LobbyAdded
import me.kcybulski.bricks.lobbies.LobbyClosed
import me.kcybulski.bricks.lobbies.LobbyDeleted
import me.kcybulski.bricks.lobbies.LobbyStartedTournament
import me.kcybulski.bricks.lobbies.PlayerJoinedToLobby
import me.kcybulski.bricks.lobbies.PlayerLeftLobby
import me.kcybulski.bricks.server.views.Avatars
import me.kcybulski.bricks.server.views.lobbies.LobbyView.Status.CLOSED
import me.kcybulski.bricks.server.views.lobbies.LobbyView.Status.IN_GAME
import me.kcybulski.bricks.server.views.lobbies.LobbyView.Status.OPEN
import me.kcybulski.bricks.tournament.GameEndedInTournament
import java.time.Clock
import java.util.UUID

class LobbiesListReadModel private constructor(
    private val clock: Clock = Clock.systemDefaultZone()
) {

    private val memory: MutableMap<String, LobbyView> = mutableMapOf()

    fun findAllLobbies(): List<LobbyView> =
        memory.values.toList()
            .sortedByDescending { it.createdAt }

    private fun onLobbyAdded(event: LobbyAdded) {
        memory[event.lobbyId.raw.toString()] = LobbyView(
            id = event.lobbyId.raw,
            name = event.lobbyName,
            image = Avatars.generateForLobby(event.lobbyId),
            playersCount = 0,
            gamesCount = 0,
            createdAt = clock.instant().toString(),
            status = OPEN
        )
    }

    private fun onLobbyDeleted(event: LobbyDeleted) {
        memory.remove(event.lobbyId.raw.toString())
    }

    private fun onPlayerJoinedToLobby(event: PlayerJoinedToLobby) {
        memory[event.lobbyId.raw.toString()]
            ?.let { it.copy(playersCount = it.playersCount + 1) }
            ?.let { memory[event.lobbyId.raw.toString()] = it }
    }

    private fun onPlayerLeftLobby(event: PlayerLeftLobby) {
        memory[event.lobbyId.raw.toString()]
            ?.let { it.copy(playersCount = it.playersCount - 1) }
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

    private fun onGameEnded(event: GameEndedInTournament) {
        memory[event.tournamentId.toString()]
            ?.let { it.copy(gamesCount = it.gamesCount + 1)}
            ?.let { memory[event.tournamentId.toString()] = it }
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
            eventBus.subscribe(GameEndedInTournament::class, lobbiesViews::onGameEnded)

            return lobbiesViews
        }

    }

}

data class LobbyView(
    val id: UUID,
    val name: String,
    val image: String,
    val playersCount: Int,
    val gamesCount: Int,
    val createdAt: String,
    val status: Status
) {

    enum class Status {

        OPEN, IN_GAME, CLOSED

    }
}
