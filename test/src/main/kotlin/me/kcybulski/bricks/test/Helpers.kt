package me.kcybulski.bricks.test

import me.kcybulski.bricks.game.Block
import me.kcybulski.bricks.game.DuoBrick

fun horizontal(x: Int, y: Int): DuoBrick =
    DuoBrick.unsafe(Block(x, y), Block(x + 1, y))

fun vertical(x: Int, y: Int): DuoBrick =
    DuoBrick.unsafe(Block(x, y), Block(x, y + 1))
