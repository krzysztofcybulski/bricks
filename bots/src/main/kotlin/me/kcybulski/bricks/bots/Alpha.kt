package me.kcybulski.bricks.bots

import me.kcybulski.bricks.api.Algorithm
import me.kcybulski.bricks.api.Block
import me.kcybulski.bricks.api.Brick
import me.kcybulski.bricks.api.DuoBrick
import me.kcybulski.bricks.api.GameInitialized
import me.kcybulski.bricks.api.Identity
import me.kcybulski.bricks.api.MoveTrigger
import me.kcybulski.bricks.api.MoveTrigger.OpponentMoved

class Alpha(name: String) : Algorithm {

    private var emptyPlaces: MutableList<Brick> = mutableListOf()

    override val identity: Identity = Identity(name)

    override suspend fun initialize(gameInitialized: GameInitialized) {
        (0 until gameInitialized.size - 1)
            .forEach { y ->
                (0 until gameInitialized.size - 1).forEach { x ->
                    emptyPlaces += horizontal(x, y)
                    emptyPlaces += vertical(x, y)
                }
            }
        (0 until gameInitialized.size - 1)
            .forEach { x -> emptyPlaces += horizontal(x, gameInitialized.size - 1) }
        (0 until gameInitialized.size - 1)
            .forEach { y -> emptyPlaces += vertical(gameInitialized.size - 1, y) }
        gameInitialized
            .initialBlocks
            .forEach { block -> emptyPlaces.removeIf { b -> block in b.blocks} }
        emptyPlaces = emptyPlaces.shuffled().toMutableList()
    }

    override suspend fun move(last: MoveTrigger): Brick {
        if (last is OpponentMoved) {
            removeEveryWith(last.brick)
        }
        return emptyPlaces
            .firstOrNull()
            ?.also(this::removeEveryWith)
            ?: horizontal(0, 0)
    }

    private fun removeEveryWith(brick: Brick) {
        emptyPlaces.removeIf { b -> b.blocks.any { it in brick.blocks } }
    }
}

private fun horizontal(x: Int, y: Int): Brick =
    DuoBrick.unsafe(Block(x, y), Block(x + 1, y))

private fun vertical(x: Int, y: Int): Brick =
    DuoBrick.unsafe(Block(x, y), Block(x, y + 1))
