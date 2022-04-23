package me.kcybulski.bricks.test

import me.kcybulski.bricks.api.Block
import me.kcybulski.bricks.api.DuoBrick

fun horizontal(x: Int, y: Int): DuoBrick =
    DuoBrick.unsafe(Block(x, y), Block(x + 1, y))

fun vertical(x: Int, y: Int): DuoBrick =
    DuoBrick.unsafe(Block(x, y), Block(x, y + 1))
