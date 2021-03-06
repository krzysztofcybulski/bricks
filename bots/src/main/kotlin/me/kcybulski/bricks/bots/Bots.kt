package me.kcybulski.bricks.bots

import me.kcybulski.bricks.api.Algorithm

class Bots(
    private val nameProvider: () -> String
) {

    private val bots = listOf(
        Bot("Alpha") { Alpha(nameProvider()) }
    )

    fun getBotNames(): List<String> = bots.map(Bot::name)

    fun getAlgorithm(name: String): Algorithm? =
        bots
            .find { it.name == name }
            ?.algorithmProvider
            ?.invoke()

    companion object {

        fun allBots(nameProvider: () -> String): Bots = Bots(nameProvider)

    }
}

internal class Bot(
    val name: String,
    val algorithmProvider: () -> Algorithm
)
