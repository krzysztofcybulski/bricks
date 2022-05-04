package me.kcybulski.bricks.client

import com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.serialization.jackson.jackson
import kotlinx.coroutines.runBlocking
import me.kcybulski.bricks.api.Block
import me.kcybulski.bricks.api.Brick
import me.kcybulski.bricks.api.DuoBrick
import me.kcybulski.bricks.api.GameInitialized
import me.kcybulski.bricks.api.MoveTrigger

class BricksWebClient(apiKey: String, host: String, port: Int = 80) {

    private val jackson = jacksonObjectMapper()

    private val httpClient = HttpClient(CIO) {
        install(WebSockets) {}
        install(ContentNegotiation) {
            jackson {
                configure(FAIL_ON_UNKNOWN_PROPERTIES, false)
            }
        }
    }

    private val websocket = WSBricksClient(httpClient, apiKey, host, port, jackson)

    private val rest = RestBricksClient(httpClient, host, port)

    suspend fun register(lobby: String, algorithm: UserAlgorithm) {
        websocket.connect(lobby, algorithm)
    }

    suspend fun register(algorithm: UserAlgorithm) {
        val lobbies = rest.getLobbies()
            .filter { it.isOpen() }

        if(lobbies.isEmpty()) {
            println("No open lobbies found")
            return
        }

        println("Choose your lobby:")
        lobbies.forEachIndexed { i, lobby ->
            println("[$i] ${lobby.name}")
        }
        register(lobbies[readLine()!!.toInt()].name, algorithm)
    }
}

fun main() = runBlocking {
    BricksWebClient("9590ff1106844852a72f25dd8f5bfd57", "localhost", 5050)
        .register(MyAlgo)
}

object MyAlgo: UserAlgorithm() {
    override suspend fun move(opponentMoved: MoveTrigger.OpponentMoved): Brick {
        return DuoBrick.unsafe(Block(2, 0), Block(2, 1))
    }

    override suspend fun firstMove(): Brick {
        return DuoBrick.unsafe(Block(0, 0), Block(1, 0))
    }

    override suspend fun initialize(gameInitialized: GameInitialized) {
    }

}