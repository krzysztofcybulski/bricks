package me.kcybulski.bricks.server.views.lobbies

import me.kcybulski.bricks.api.Identity
import me.kcybulski.bricks.events.EventBus
import me.kcybulski.bricks.game.GameEndedEvent
import me.kcybulski.bricks.game.GameStartedEvent
import me.kcybulski.bricks.game.GameWonResult
import me.kcybulski.bricks.game.TieResult
import me.kcybulski.bricks.lobbies.LobbyAdded
import me.kcybulski.bricks.lobbies.LobbyClosed
import me.kcybulski.bricks.lobbies.LobbyDeleted
import me.kcybulski.bricks.lobbies.LobbyId
import me.kcybulski.bricks.lobbies.LobbyStartedTournament
import me.kcybulski.bricks.lobbies.PlayerJoinedToLobby
import me.kcybulski.bricks.lobbies.PlayerLeftLobby
import me.kcybulski.bricks.server.views.Avatars
import me.kcybulski.bricks.server.views.lobbies.LobbyDetailsView.Game
import me.kcybulski.bricks.server.views.lobbies.LobbyDetailsView.Player
import me.kcybulski.bricks.server.views.lobbies.LobbyDetailsView.Status.CLOSED
import me.kcybulski.bricks.server.views.lobbies.LobbyDetailsView.Status.IN_GAME
import me.kcybulski.bricks.server.views.lobbies.LobbyDetailsView.Status.OPEN
import me.kcybulski.bricks.server.views.users.UserViewsReadModel
import me.kcybulski.bricks.tournament.GameEndedInTournament
import me.kcybulski.bricks.tournament.TournamentEnded
import java.util.UUID

class LobbyDetailsReadModel private constructor(
    private val users: UserViewsReadModel
) {

    private val gamesMemory: MutableMap<String, Game> = mutableMapOf()
    private val memory: MutableMap<String, LobbyDetailsView> = mutableMapOf()

    fun findLobby(id: LobbyId): LobbyDetailsView? =
        memory[id.raw.toString()]

    private fun onLobbyAdded(event: LobbyAdded) {
        memory[event.lobbyId.raw.toString()] = LobbyDetailsView(
            id = event.lobbyId.raw,
            name = event.lobbyName,
            image = Avatars.generateForLobby(event.lobbyId),
            players = emptyList(),
            status = OPEN,
            games = emptyList()
        )
    }

    private fun onLobbyDeleted(event: LobbyDeleted) {
        memory.remove(event.lobbyId.raw.toString())
    }

    private fun onPlayerJoinedToLobby(event: PlayerJoinedToLobby) {
        memory[event.lobbyId.raw.toString()]
            ?.let { it.copy(players = it.players + findUser(event.player)) }
            ?.let { memory[event.lobbyId.raw.toString()] = it }
    }

    private fun defaultPlayer(player: Identity) =
        Player(player.name, player.name, Avatars.generateForPlayer(player), 0)

    private fun onPlayerLeftLobby(event: PlayerLeftLobby) {
        memory[event.lobbyId.raw.toString()]
            ?.let { it.copy(players = it.players.filter { p -> p.name != event.player.name }) }
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

    private fun onGameEndedInTournament(event: GameEndedInTournament) {
        memory[event.tournamentId.toString()]
            ?.let { gamesMemory[event.gameId.toString()]?.let { game -> it.copy(games = it.games + game) } }
            ?.let { memory[event.tournamentId.toString()] = it }
    }

    private fun onGameStarted(event: GameStartedEvent) {
        gamesMemory[event.gameId.toString()] = Game(
            id = event.gameId,
            players = listOf(findUser(event.players.first), findUser(event.players.second)),
            winner = null
        )
    }

    private fun onGameEnded(event: GameEndedEvent) {
        gamesMemory[event.gameId.toString()]
            ?.copy(
                winner = when (event.result) {
                    TieResult -> "-"
                    is GameWonResult -> (event.result as GameWonResult).player.name
                }
            )
            ?.let { gamesMemory[event.gameId.toString()] = it }
    }

    private fun onTournamentEnded(event: TournamentEnded) {
        memory[event.tournamentId.toString()]
            ?.let { lobby ->
                lobby.copy(
                    players = lobby.players.map { player ->
                        player.copy(points = lobby.games.count { it.winner == player.name })
                    }
                )
            }
            ?.let { memory[event.tournamentId.toString()] = it }
    }

    private fun findUser(identity: Identity) = users.find(identity.name)
        ?.let { Player(it.id, it.name, it.avatarUrl, 0) }
        ?: defaultPlayer(identity)

    companion object {

        fun configureInMemory(eventBus: EventBus, users: UserViewsReadModel): LobbyDetailsReadModel {
            val lobbiesViews = LobbyDetailsReadModel(users)

            eventBus.subscribe(LobbyAdded::class, lobbiesViews::onLobbyAdded)
            eventBus.subscribe(LobbyDeleted::class, lobbiesViews::onLobbyDeleted)
            eventBus.subscribe(PlayerJoinedToLobby::class, lobbiesViews::onPlayerJoinedToLobby)
            eventBus.subscribe(PlayerLeftLobby::class, lobbiesViews::onPlayerLeftLobby)
            eventBus.subscribe(LobbyStartedTournament::class, lobbiesViews::onLobbyStartedTournament)
            eventBus.subscribe(LobbyClosed::class, lobbiesViews::onLobbyClosed)
            eventBus.subscribe(GameEndedInTournament::class, lobbiesViews::onGameEndedInTournament)
            eventBus.subscribe(GameStartedEvent::class, lobbiesViews::onGameStarted)
            eventBus.subscribe(GameEndedEvent::class, lobbiesViews::onGameEnded)
            eventBus.subscribe(TournamentEnded::class, lobbiesViews::onTournamentEnded)

            return lobbiesViews
        }

    }

}

data class LobbyDetailsView(
    val id: UUID,
    val name: String,
    val image: String,
    val players: List<Player>,
    val games: List<Game>,
    val status: Status
) {

    enum class Status {

        OPEN, IN_GAME, CLOSED

    }

    data class Player(
        val id: String,
        val name: String,
        val avatarUrl: String,
        val points: Int
    )

    data class Game(
        val id: UUID,
        val players: List<Player>,
        val winner: String?
    )

}
