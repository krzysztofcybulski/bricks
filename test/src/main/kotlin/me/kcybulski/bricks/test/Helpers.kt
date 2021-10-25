package me.kcybulski.bricks.test

import arrow.core.Either
import arrow.core.getOrHandle
import arrow.core.left
import me.kcybulski.bricks.game.Block
import me.kcybulski.bricks.game.Brick
import me.kcybulski.bricks.game.DuoBrick
import me.kcybulski.bricks.game.InvalidBrick

fun horizontal(x: Int, y: Int): Either<InvalidBrick, Brick> {
    val first: Block = Block.of(x, y).getOrHandle { return it.left() }
    val second: Block = Block.of(x + 1, y).getOrHandle { return it.left() }
    return DuoBrick.of(first, second)
}

fun vertical(x: Int, y: Int): Either<InvalidBrick, Brick> {
    val first: Block = Block.of(x, y).getOrHandle { return it.left() }
    val second: Block = Block.of(x, y + 1).getOrHandle { return it.left() }
    return DuoBrick.of(first, second)
}
