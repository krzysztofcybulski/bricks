package me.kcybulski.bricks.server

import kotlinx.coroutines.job
import kotlinx.coroutines.runBlocking

fun main() = runBlocking {
    val (_, _, entrance, refreshLobbies, server) = Configuration.app(this)

    entrance.newLobby()

    server.start()
    coroutineContext.job.join()
}
