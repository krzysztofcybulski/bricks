package me.kcybulski.bricks.api

import me.kcybulski.bricks.api.DuoBrickResult.InvalidBlocks
import me.kcybulski.bricks.api.DuoBrickResult.InvalidPosition
import kotlin.math.abs

sealed class Brick(val blocks: List<Block>)

class DuoBrick private constructor(blocks: List<Block>) : Brick(blocks), DuoBrickResult {

    companion object {

        fun safe(first: Block, second: Block): DuoBrickResult = when {
            !first.isNextTo(second) -> InvalidBlocks
            first.outOfMap() || second.outOfMap() -> InvalidPosition
            else -> DuoBrick(listOf(first, second))
        }

        fun unsafe(first: Block, second: Block): DuoBrick =
            safe(first, second)
                .takeIf { it is DuoBrick }
                ?.let { it as DuoBrick }
                ?: throw IllegalArgumentException("Invalid block positions")

    }
}

data class Block(val x: Int, val y: Int) {

    fun isNextTo(block: Block) = (abs(x - block.x) == 1) xor (abs(y - block.y) == 1)

    internal fun outOfMap() = x < 0 || y < 0
}

sealed interface DuoBrickResult {

    object InvalidPosition : DuoBrickResult
    object InvalidBlocks : DuoBrickResult

}
