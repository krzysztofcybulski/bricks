package me.kcybulski.bricks.server.lobby

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay

class Healthchecker private constructor(
    private val entrance: Entrance,
    private val healthcheckDelay: Long = 1000L
) {

    suspend fun start() = coroutineScope {
        while (true) {
            entrance
                .lobbies()
                .filterIsInstance<OpenLobby>()
                .forEach { it.refresh() }
            delay(healthcheckDelay)
        }
    }

    companion object {

        suspend fun startForEntrance(entrance: Entrance) {
            Healthchecker(entrance).start()
        }

    }
}
