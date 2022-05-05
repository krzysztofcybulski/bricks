package me.kcybulski.bricks.server.healthcheck

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext
import me.kcybulski.bricks.events.CommandBus
import me.kcybulski.bricks.lobbies.KickPlayerCommand
import me.kcybulski.bricks.server.Healthy
import me.kcybulski.bricks.server.NotHealthy
import me.kcybulski.bricks.server.api.lobbies.WebsocketsRegistry

class Healthchecker private constructor(
    private val websocketsRegistry: WebsocketsRegistry,
    private val commandBus: CommandBus,
    private val refreshLobbies: RefreshLobbies,
    private val coroutine: CoroutineScope = CoroutineScope(newSingleThreadContext("healthcheck")),
    private val healthcheckDelay: Long = 5000L
) {

    fun start() = coroutine.launch {
        while (true) {
            val healthStatuses = collectHealthStatuses()
            healthStatuses
                .filter { (_, v) -> v is NotHealthy }
                .forEach { (k, _) -> commandBus.send(KickPlayerCommand(k.lobbyId, k.identity)) }
            refreshLobbies.reportPing(
                healthStatuses
                    .filterValues { it is Healthy }
                    .map { (k, v) -> k.identity to (v as Healthy).answerInMillis }
                    .toMap()
            )
            delay(healthcheckDelay)
        }
    }

    private suspend fun collectHealthStatuses() =
        websocketsRegistry
            .findAll()
            .associateWith { it.healthStatus() }

    companion object {

        fun configure(
            websocketsRegistry: WebsocketsRegistry,
            refreshLobbies: RefreshLobbies,
            commandBus: CommandBus,
        ) = Healthchecker(websocketsRegistry, commandBus, refreshLobbies)

    }
}
