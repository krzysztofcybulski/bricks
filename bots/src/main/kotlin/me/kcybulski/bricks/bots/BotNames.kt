package me.kcybulski.bricks.bots

import com.github.javafaker.Faker

internal object BotNames {

    private val faker = Faker()

    fun name(): String = "Bot ${faker.funnyName().name()}"

}
