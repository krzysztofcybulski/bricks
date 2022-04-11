package me.kcybulski.bricks.client

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

internal class RestBricksClient(
    private val httpClient: HttpClient,
    private val host: String,
    private val port: Int
) {

    private val url = "http://$host:$port"

    suspend fun getLobbies(): List<Lobby> = httpClient.get(url).body()

}

internal data class Lobby(
    val name: String,
    val status: String
) {

    fun isOpen() = status == "OPEN"

}
