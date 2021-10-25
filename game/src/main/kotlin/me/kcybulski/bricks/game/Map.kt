package me.kcybulski.bricks.game

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import me.kcybulski.bricks.game.PlacingError.CannotPlaceOnTakenField
import me.kcybulski.bricks.game.PlacingError.CannotPlaceOutOfMap

class Map private constructor(private val map: Array<Array<Field>>) {

    fun place(brick: Brick): Either<PlacingError, Map> = when {
        !isOnField(brick) -> CannotPlaceOutOfMap(brick).left()
        !isFree(brick) -> CannotPlaceOnTakenField(brick).left()
        else -> withBrick(brick).right()
    }

    private fun withBrick(brick: Brick) = Map(
        brick.blocks.fold(map) { map, pos -> map.with(pos, BlockField) }
    )

    private fun isOnField(brick: Brick) = brick.blocks.all { it.y < map.size && it.x < map.first().size }

    private fun isFree(brick: Brick) = brick.blocks.all { map[it.y][it.x] == EmptyField }

    companion object {
        fun of(size: Int) = Map(
            Array(size) { Array(size) { EmptyField } }
        )
    }
}

sealed class Field

object EmptyField : Field()
object BlockField : Field()

sealed class PlacingError(val brick: Brick) {

    class CannotPlaceOutOfMap(brick: Brick) : PlacingError(brick)
    class CannotPlaceOnTakenField(brick: Brick) : PlacingError(brick)

}

private inline fun <reified T> Array<Array<T>>.with(block: Block, new: T): Array<Array<T>> =
    with(block.y) { it.with(block.x) { new } }

private inline fun <reified T> Array<T>.with(position: Int, new: (T) -> T): Array<T> =
    mapIndexed { index, old -> if (index == position) new(old) else old }
        .toTypedArray()
