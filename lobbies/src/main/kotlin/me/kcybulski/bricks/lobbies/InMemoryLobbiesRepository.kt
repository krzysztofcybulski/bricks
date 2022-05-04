package me.kcybulski.bricks.lobbies

internal class InMemoryLobbiesRepository: LobbiesRepository {

    private val memory: MutableMap<LobbyId, Lobby> = mutableMapOf()

    override suspend fun find(id: LobbyId): Lobby? = memory[id]

    override suspend fun save(lobby: Lobby): Lobby {
        memory[lobby.id] = lobby
        return lobby
    }

    override suspend fun delete(id: LobbyId) {
        memory -= id
    }


}