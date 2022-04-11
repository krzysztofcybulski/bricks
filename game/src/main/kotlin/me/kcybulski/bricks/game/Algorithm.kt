package me.kcybulski.bricks.game

import com.github.javafaker.Faker
import java.util.UUID

sealed class MoveTrigger {

    object FirstMove : MoveTrigger()
    data class OpponentMoved(val brick: Brick): MoveTrigger()

}

data class GameInitialized(
    val gameId: UUID,
    val size: Int,
    val players: PlayersPair,
    val initialBlocks: Set<Block>
)

interface Algorithm {

    val identity: Identity
    suspend fun initialize(gameInitialized: GameInitialized)
    suspend fun move(last: MoveTrigger): Brick

}

data class Identity(val name: String) {

    companion object {
        fun default() = Identity(Faker().animal().name())
    }
}
