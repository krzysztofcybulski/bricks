package me.kcybulski.bricks.server.utils

import com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.plugins.websocket.webSocketSession
import io.ktor.client.request.parameter
import io.ktor.http.HttpMethod.Companion.Get
import io.ktor.serialization.jackson.jackson
import io.ktor.websocket.Frame
import io.ktor.websocket.readText

class TestWebsocket(
    private val host: String = "localhost",
    private val port: Int,
    private val params: List<Pair<String, String>> = emptyList()
) : AutoCloseable {

    private val jackson = jacksonObjectMapper()

    private lateinit var websocket: DefaultClientWebSocketSession

    private val http = HttpClient(CIO) {
        install(WebSockets) {}
        install(ContentNegotiation) {
            jackson {
                configure(FAIL_ON_UNKNOWN_PROPERTIES, false)
            }
        }
    }

    suspend fun connect(path: String) {
        websocket = http.webSocketSession(
            method = Get,
            host = host,
            port = port,
            path = path,
            block = {
                params.forEach {
                    (key, value) -> parameter(key, value)
                }
            }
        )
    }

    suspend fun last(): Map<String, *>? =
        websocket
            .incoming
            .receiveCatching().getOrNull()
            ?.let { it as Frame.Text }
            ?.let { jackson.readValue(it.readText(), Map::class.java) as Map<String, *> }

    override fun close() {
        http.close()
    }
}
