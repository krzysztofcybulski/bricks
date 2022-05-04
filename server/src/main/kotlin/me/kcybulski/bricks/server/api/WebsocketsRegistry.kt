package me.kcybulski.bricks.server.api

import me.kcybulski.bricks.server.PlayerConnection
import ratpack.websocket.WebSocket

class WebsocketsRegistry(
    private val players: MutableSet<PlayerConnection> = mutableSetOf()
) {

    fun register(playerConnection: PlayerConnection) {
        players += playerConnection
    }

    fun find(webSocket: WebSocket) = players.find { it.webSocket == webSocket }

}