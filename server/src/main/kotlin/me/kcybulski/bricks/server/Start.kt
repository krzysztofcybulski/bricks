package me.kcybulski.bricks.server

import kotlinx.coroutines.job
import kotlinx.coroutines.runBlocking
import me.kcybulski.bricks.lobbies.CreateLobbyCommand

fun main() = runBlocking {
    val (commandBus, server) = Configuration.app(this)

    commandBus.send(CreateLobbyCommand())

    server.start()
    coroutineContext.job.join()
}
