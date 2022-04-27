package me.kcybulski.bricks.server

import kotlinx.coroutines.job
import kotlinx.coroutines.runBlocking
import me.kcybulski.bricks.server.lobby.Healthchecker

fun main() = runBlocking {
    val (_, _, entrance, refreshLobbies, server) = Configuration.app(this)

    entrance.newLobby()

    server.start()

    Healthchecker.startForEntrance(entrance, refreshLobbies)

    coroutineContext.job.join()
}
