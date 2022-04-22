package me.kcybulski.bricks.bots

import me.kcybulski.bricks.game.Algorithm

class Bots {

    private val bots = listOf(
        Bot("Alpha", ::Alpha)
    )

    fun getBotNames(): List<String> = bots
        .map(Bot::name)

    fun getAlgorithm(name: String): Algorithm? =
        bots
            .find { it.name == name }
            ?.algorithmProvider
            ?.invoke()

    companion object {

        fun allBots(): Bots = Bots()

    }
}

internal class Bot(
    val name: String,
    val algorithmProvider: () -> Algorithm
)
