package me.kcybulski.bricks.lobbies

import me.kcybulski.bricks.events.CommandBus
import me.kcybulski.bricks.events.EventBus
import java.util.UUID

class LobbiesModule private constructor(
    private val factory: LobbyFactory,
    private val repository: LobbiesRepository,
    private val eventBus: EventBus
) {

    suspend fun create(command: CreateLobbyCommand) {
        val lobby = factory.create(command)
        repository.save(lobby)
        eventBus.send(LobbyAdded(lobby.id, lobby.name), lobby.id.toString())
    }

    suspend fun delete(command: DeleteLobbyCommand) {
        repository.delete(command.id)
        eventBus.send(LobbyDeleted(command.id), command.id.toString())
    }

    suspend fun join(command: JoinLobbyCommand) {
        repository
            .find(command.id)
            ?.let { it as? OpenLobby }
            ?.join(command.algorithm)
            ?.let { repository.save(it) }
    }

    suspend fun startTournament(command: StartTournamentCommand) {
        repository
            .find(command.id)
            ?.let { it as? OpenLobby }
            ?.start(command.settings)
            ?.also { repository.save(it) }
            ?.close()
            ?.let { repository.save(it) }
    }

    companion object {

        fun configureInMemory(
            commandBus: CommandBus,
            eventBus: EventBus,
            lobbyNameGenerator: () -> String
        ) {
            val module = LobbiesModule(
                factory = LobbyFactory(lobbyNameGenerator, eventBus, commandBus),
                repository = InMemoryLobbiesRepository(),
                eventBus = eventBus
            )

            commandBus.on(CreateLobbyCommand::class, module::create)
            commandBus.on(StartTournamentCommand::class, module::startTournament)
            commandBus.on(DeleteLobbyCommand::class, module::delete)
            commandBus.on(JoinLobbyCommand::class, module::join)
        }

    }

}