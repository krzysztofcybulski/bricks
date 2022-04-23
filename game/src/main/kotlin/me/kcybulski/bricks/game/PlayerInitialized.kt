package me.kcybulski.bricks.game

import me.kcybulski.bricks.api.Identity

sealed class PlayerInitialized(val player: Identity)

class PlayerExceededInitTimeout(player: Identity) : PlayerInitialized(player)
class PlayerInitializedInTime(player: Identity) : PlayerInitialized(player)

fun List<PlayerInitialized>.allInitializedInTime() = all { it is PlayerInitializedInTime }
fun List<PlayerInitialized>.allExceededTime() = all { it is PlayerExceededInitTimeout }

fun List<PlayerInitialized>.playerThatInitializedInTime() = filterIsInstance<PlayerInitializedInTime>()
    .also { require(it.size == 1) { "More than one player initialized in time" } }
    .first()
    .player
