package me.kcybulski.bricks.server.lobby

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.Executors

class Healthchecker(
    private val entrance: Entrance
) {

    private val scope = CoroutineScope(
        Executors.newSingleThreadExecutor()
            .asCoroutineDispatcher()
    )

    fun start() {
        scope.launch {
            while(true) {
                entrance
                    .lobbies()
                    .filterIsInstance<OpenLobby>()
                    .forEach { it.refresh() }
                delay(500)
            }
        }
    }

}
