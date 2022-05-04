package me.kcybulski.bricks.lobbies

import me.kcybulski.bricks.api.Algorithm
import me.kcybulski.bricks.api.Identity
import me.kcybulski.bricks.events.CommandBus
import me.kcybulski.bricks.events.EventBus
import me.kcybulski.bricks.tournament.StartNewTournament
import me.kcybulski.bricks.tournament.TournamentSettings
import java.util.UUID

@JvmInline
value class LobbyId(val raw: UUID)

internal sealed class Lobby(
    val id: LobbyId
)

internal class OpenLobby(
    id: LobbyId,
    val name: String,
    private val players: List<Algorithm>,
    private val eventBus: EventBus,
    private val commandBus: CommandBus
): Lobby(id) {

    fun join(algorithm: Algorithm): OpenLobby {
        eventBus.send(PlayerJoinedToLobby(id, algorithm.identity.name), id.toString())
        return OpenLobby(id, name, players + algorithm, eventBus, commandBus)
    }

    fun kick(identity: Identity): OpenLobby =
        if(identity in players.map(Algorithm::identity)) {
            eventBus.send(PlayerLeftLobby(id, identity.name), id.toString())
            OpenLobby(id, name, players.filterNot { it.identity == identity }, eventBus, commandBus)
        } else {
            this
        }

    fun start(settings: TournamentSettings): InGameLobby {
        commandBus.send(StartNewTournament(id.raw, players, settings))
        eventBus.send(LobbyStartedTournament(id, settings), id.toString())
        return InGameLobby(id, eventBus)
    }

}

internal class InGameLobby(
    id: LobbyId,
    private val eventBus: EventBus
): Lobby(id) {

    fun close(): ClosedLobby {
        eventBus.send(LobbyClosed(id), id.toString())
        return ClosedLobby(id)
    }
}

internal class ClosedLobby(
    id: LobbyId
): Lobby(id)