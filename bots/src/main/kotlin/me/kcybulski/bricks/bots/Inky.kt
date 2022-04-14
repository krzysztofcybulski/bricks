package me.kcybulski.bricks.bots

import arrow.core.Either
import arrow.core.getOrHandle
import me.kcybulski.bricks.game.Algorithm
import me.kcybulski.bricks.game.Block
import me.kcybulski.bricks.game.Brick
import me.kcybulski.bricks.game.DuoBrick
import me.kcybulski.bricks.game.GameInitialized
import me.kcybulski.bricks.game.Identity
import me.kcybulski.bricks.game.InvalidBrick
import me.kcybulski.bricks.game.MoveTrigger

class Inky : Algorithm {

    private var emptyPlaces: MutableList<Brick> = mutableListOf()

    override val identity: Identity = Identity(BotNames.name())

    override suspend fun initialize(gameInitialized: GameInitialized) {
        (0 until gameInitialized.size - 1)
            .forEach { y ->
                (0 until gameInitialized.size - 1).forEach { x ->
                    horizontal(x, y).tap { emptyPlaces += it }
                    vertical(x, y).tap { emptyPlaces += it }
                }
            }
        gameInitialized
            .initialBlocks
            .forEach { block -> emptyPlaces.removeIf { b -> block in b.blocks} }
        emptyPlaces = emptyPlaces.shuffled().toMutableList()
    }

    override suspend fun move(last: MoveTrigger): Brick {
        if (last is MoveTrigger.OpponentMoved) {
            removeEveryWith(last.brick)
        }
        return emptyPlaces
            .firstOrNull()
            ?.also(this::removeEveryWith)
            ?: horizontal(0, 0).get()
    }

    private fun removeEveryWith(brick: Brick) {
        emptyPlaces.removeIf { b -> b.blocks.any { it in brick.blocks } }
    }
}

private fun <T> Either<InvalidBrick, T>.get() = getOrHandle { throw IllegalArgumentException() }

private fun horizontal(x: Int, y: Int): Either<InvalidBrick, Brick> =
    DuoBrick.of(Block(x, y), Block(x + 1, y))

private fun vertical(x: Int, y: Int): Either<InvalidBrick, Brick> =
    DuoBrick.of(Block(x, y), Block(x, y + 1))
