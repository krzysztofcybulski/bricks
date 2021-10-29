package me.kcybulski.bricks.game

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import me.kcybulski.bricks.game.InvalidBrick.InvalidBlocks
import me.kcybulski.bricks.game.InvalidBrick.InvalidPosition
import kotlin.math.abs

sealed class Brick(val blocks: List<Block>)

class DuoBrick private constructor(blocks: List<Block>) : Brick(blocks) {

    companion object {
        fun of(first: Block, second: Block): Either<InvalidBrick, Brick> = when {
            !first.isNextTo(second) -> InvalidBlocks.left()
            first.outOfMap() || second.outOfMap() -> InvalidPosition.left()
            else -> DuoBrick(listOf(first, second)).right()
        }
    }
}

data class Block(val x: Int, val y: Int) {

    fun isNextTo(block: Block) = (abs(x - block.x) == 1) xor (abs(y - block.y) == 1)

    internal fun outOfMap() = x < 0 || y < 0
}

sealed class InvalidBrick {

    object InvalidPosition : InvalidBrick()
    object InvalidBlocks : InvalidBrick()

}
