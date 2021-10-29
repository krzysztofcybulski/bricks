package me.kcybulski.bricks.client

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.features.websocket.WebSockets
import me.kcybulski.bricks.game.Algorithm

class BricksWebClient(host: String, port: Int) {

    private val websocket = WSBricksClient(HttpClient(CIO) {
        install(WebSockets)
    }, host, port, jacksonObjectMapper())

    fun register(algorithm: Algorithm) {
        websocket.connect(algorithm)
    }
}
