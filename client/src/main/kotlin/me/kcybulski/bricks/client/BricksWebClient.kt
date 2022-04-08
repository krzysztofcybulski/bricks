package me.kcybulski.bricks.client

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.websocket.WebSockets
import me.kcybulski.bricks.game.Algorithm

class BricksWebClient(host: String, port: Int = 80) {

    private val jackson = jacksonObjectMapper()

    private val httpClient = HttpClient(CIO) {
        install(WebSockets)
        install(JsonFeature)
    }

    private val websocket = WSBricksClient(httpClient, host, port, jackson)

    private val rest = RestBricksClient(httpClient, host, port)

    suspend fun register(lobby: String, algorithm: Algorithm) {
        websocket.connect(lobby, algorithm)
    }

    suspend fun register(algorithm: Algorithm) {
        val lobbies = rest.getLobbies()
            .filter { it.isOpen() }
        println("Choose your lobby:")
        lobbies.forEachIndexed { i, lobby ->
            println("[$i] ${lobby.name}")
        }
        register(lobbies[readLine()!!.toInt()].name, algorithm)
    }
}
