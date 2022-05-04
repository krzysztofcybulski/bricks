package me.kcybulski.bricks.server.lobby

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import me.kcybulski.bricks.server.Healthy
import me.kcybulski.bricks.server.NotHealthy

//class Healthchecker private constructor(
//    private val entrance: Entrance,
//    private val refreshLobbies: RefreshLobbies,
//    private val healthcheckDelay: Long = 2000L
//) {
//
//    suspend fun start() = coroutineScope {
//        while (true) {
//            entrance
//                .lobbies()
//                .filterIsInstance<OpenLobby>()
//                .forEach { refreshLobby(it) }
//            delay(healthcheckDelay)
//        }
//    }
//
//    private suspend fun refreshLobby(openLobby: OpenLobby) {
//        val healthStatuses = openLobby.getHealthStatuses()
//        healthStatuses
//            .filterValues { it is Healthy }
//            .takeIf { it.isNotEmpty() }
//            ?.let { connections ->
//                refreshLobbies.reportPing(
//                    connections
//                        .map { (k, v) -> k.identity to (v as Healthy).answerInMillis }
//                        .toMap()
//                )
//            }
//        healthStatuses
//            .filterValues { it is NotHealthy }
//            .forEach { openLobby.kick(it.key) }
//    }
//
//    companion object {
//
//        suspend fun startForEntrance(entrance: Entrance, refreshLobbies: RefreshLobbies) = coroutineScope {
//            launch {
//                Healthchecker(entrance, refreshLobbies).start()
//            }
//        }
//
//    }
//}
