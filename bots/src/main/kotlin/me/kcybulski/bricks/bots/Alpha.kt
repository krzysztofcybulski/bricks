package me.kcybulski.bricks.bots

import me.kcybulski.bricks.game.Algorithm
import me.kcybulski.bricks.game.Block
import me.kcybulski.bricks.game.Brick
import me.kcybulski.bricks.game.DuoBrick
import me.kcybulski.bricks.game.GameInitialized
import me.kcybulski.bricks.game.Identity
import me.kcybulski.bricks.game.MoveTrigger
import me.kcybulski.bricks.game.MoveTrigger.OpponentMoved

class Alpha : Algorithm {

    private var emptyPlaces: MutableList<Brick> = mutableListOf()

    override val identity: Identity = Identity(BotNames.name())

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
