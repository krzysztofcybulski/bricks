package me.kcybulski.bricks.server.api

import me.kcybulski.bricks.game.GameSettings
import me.kcybulski.bricks.tournament.TournamentSettings

data class StartRequest(
    val sizes: List<Int> = listOf(5, 9, 16),
    val initTime: Long = 1000,
    val moveTime: Long = 1000
) {

    fun toSettings() = TournamentSettings(
        GameSettings(initTime, moveTime),
        sizes
    )

}
