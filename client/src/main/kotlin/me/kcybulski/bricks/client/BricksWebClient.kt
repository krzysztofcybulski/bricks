package me.kcybulski.bricks.client

import com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.ktor.client.HttpClient
import io.ktor.client.call.NoTransformationFoundException
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.serialization.jackson.jackson

class BricksWebClient(host: String, port: Int = 80) {

    private val jackson = jacksonObjectMapper()

    private val httpClient = HttpClient(CIO) {
        install(WebSockets) {}
        install(ContentNegotiation) {
            jackson {
                configure(FAIL_ON_UNKNOWN_PROPERTIES, false)
            }
        }
    }

    private val websocket = WSBricksClient(httpClient, host, port, jackson)

    private val rest = RestBricksClient(httpClient, host, port)

    suspend fun register(apiKey: String, lobbyId: String, algorithm: UserAlgorithm) {
        websocket.connect(apiKey, lobbyId, algorithm)
    }

    suspend fun register(algorithm: UserAlgorithm) {
        val lobbies = rest.getLobbies()
            .filter { it.isOpen() }

        if (lobbies.isEmpty()) {
            println("No open lobbies found")
            return
        }

        println("Choose your lobby:")
        lobbies.forEachIndexed { i, lobby ->
            println("[$i] ${lobby.name}")
        }

        val lobbyIndex = readLine()!!.toInt()

        if(lobbyIndex >= lobbies.size) {
            println("Invalid lobby")
            return register(algorithm)
        }

        try {
            return register(
                getApiKey(),
                lobbies[lobbyIndex].id,
                algorithm
            )
        } catch(e: NoTransformationFoundException) {
            println("Invalid api key")
            return register(algorithm)
        }
    }

    private fun getApiKey(): String {
        val envApiKey = System.getenv("BRICKS_API_KEY")
        return if(envApiKey != null) {
            println("Loaded API_KEY from environment variable")
            envApiKey
        } else {
            println("Enter your api key:")
            readLine()!!
        }
    }
}
