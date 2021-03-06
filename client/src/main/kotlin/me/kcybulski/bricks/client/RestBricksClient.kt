package me.kcybulski.bricks.client

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

internal class RestBricksClient(
    private val httpClient: HttpClient,
    host: String,
    port: Int
) {

    private val url = "http://$host:$port/lobbies"

    suspend fun getLobbies(): List<Lobby> = httpClient.get(url).body()

}

internal data class Lobby(
    val id: String,
    val name: String,
    val status: String
) {

    fun isOpen() = status == "OPEN"

}
