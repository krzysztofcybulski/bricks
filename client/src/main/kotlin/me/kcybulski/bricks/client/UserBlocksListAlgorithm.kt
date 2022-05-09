package me.kcybulski.bricks.client

import me.kcybulski.bricks.api.Block
import me.kcybulski.bricks.api.Brick
import me.kcybulski.bricks.api.GameInitialized
import me.kcybulski.bricks.api.MoveTrigger
import me.kcybulski.bricks.api.MoveTrigger.FirstMove
import me.kcybulski.bricks.api.MoveTrigger.OpponentMoved

abstract class UserBlocksListAlgorithm(
    suffix: String = ""
) : UserAlgorithm(suffix) {

    private val takenBlocks: MutableSet<Block> = mutableSetOf()
    private var mapSize = 0

    final override suspend fun initialize(gameInitialized: GameInitialized) {
        mapSize = gameInitialized.size
        gameInitialized.initialBlocks.forEach { takenBlocks += it }
    }

    final override suspend fun move(last: MoveTrigger): Brick =
        when (last) {
            FirstMove -> move(takenBlocks, mapSize)
            is OpponentMoved -> {
                save(last.brick)
                move(takenBlocks, mapSize)
            }
        }
            .also(this::save)

    abstract suspend fun move(takenBlocks: Set<Block>, mapSize: Int): Brick

    private fun save(brick: Brick) {
        brick.blocks.forEach { takenBlocks += it }
    }
}
