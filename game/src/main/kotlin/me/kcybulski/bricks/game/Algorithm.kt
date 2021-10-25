package me.kcybulski.bricks.game

import com.github.javafaker.Faker
import me.kcybulski.bricks.game.Identity.Companion.default

sealed class MoveTrigger {

    object FirstMove : MoveTrigger()
    data class OpponentMoved(val brick: Brick): MoveTrigger()

}

interface Algorithm {

    val identity: Identity
        get() = default()

    suspend fun initialize(game: NewGame): Unit
    suspend fun move(last: MoveTrigger): Brick

}

data class Identity(val name: String) {

    companion object {
        fun default() = Identity(Faker().animal().name())
    }
}
