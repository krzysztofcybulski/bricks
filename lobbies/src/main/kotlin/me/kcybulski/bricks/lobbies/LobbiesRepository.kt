package me.kcybulski.bricks.lobbies

internal interface LobbiesRepository {

    suspend fun find(id: LobbyId): Lobby?

    suspend fun save(lobby: Lobby): Lobby

    suspend fun delete(id: LobbyId)

}