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
        fun of(vararg blocks: Block): Either<InvalidBrick, Brick> = when {
            blocks.size != 2 -> InvalidBlocks.left()
            notNextToEach(blocks) -> InvalidBlocks.left()
            else -> DuoBrick(blocks.toList()).right()
        }

        private fun notNextToEach(blocks: Array<out Block>) = !blocks[0].isNextTo(blocks[1])
    }
}

class Block private constructor(val x: Int, val y: Int) {

    fun isNextTo(block: Block) = (abs(x - block.x) == 1) xor (abs(y - block.y) == 1)

    companion object {
        fun of(x: Int, y: Int): Either<InvalidBrick, Block> = when {
            x < 0 || y < 0 -> InvalidPosition.left()
            else -> Block(x, y).right()
        }
    }
}

sealed class InvalidBrick {

    object InvalidPosition : InvalidBrick()
    object InvalidBlocks : InvalidBrick()

}
