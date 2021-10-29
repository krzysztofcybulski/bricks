package me.kcybulski.bricks.test

import arrow.core.Either
import me.kcybulski.bricks.game.Block
import me.kcybulski.bricks.game.Brick
import me.kcybulski.bricks.game.DuoBrick
import me.kcybulski.bricks.game.InvalidBrick

fun horizontal(x: Int, y: Int): Either<InvalidBrick, Brick> =
    DuoBrick.of(Block(x, y), Block(x + 1, y))

fun vertical(x: Int, y: Int): Either<InvalidBrick, Brick> =
    DuoBrick.of(Block(x, y), Block(x, y + 1))
