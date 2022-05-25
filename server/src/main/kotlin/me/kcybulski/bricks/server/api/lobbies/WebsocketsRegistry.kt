package me.kcybulski.bricks.server.api.lobbies

import me.kcybulski.bricks.server.PlayerConnection
import ratpack.websocket.WebSocket

class WebsocketsRegistry(
    private val players: MutableSet<PlayerConnection> = mutableSetOf()
) {

    fun findAll() = players.toSet()

    fun register(playerConnection: PlayerConnection) {
        players += playerConnection
    }

    fun remove(playerConnection: PlayerConnection) {
        players -= playerConnection
    }

    fun find(webSocket: WebSocket) = players.find { it.webSocket == webSocket }

}