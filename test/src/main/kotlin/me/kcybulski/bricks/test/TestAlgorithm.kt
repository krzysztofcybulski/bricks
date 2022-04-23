package me.kcybulski.bricks.test

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import me.kcybulski.bricks.api.Algorithm
import me.kcybulski.bricks.api.Brick
import me.kcybulski.bricks.api.GameInitialized
import me.kcybulski.bricks.api.Identity
import me.kcybulski.bricks.api.MoveTrigger

class TestAlgorithm(
    name: String
) : Algorithm {

    var initTime = 0L
    private val moves: MutableList<suspend () -> Brick> = mutableListOf()

    private lateinit var defaultMove: suspend () -> Brick

    fun moves(vararg moves: Brick): TestAlgorithm {
        this.moves.clear()
        this.moves += moves.map { { it } }
        return this
    }

    fun nextMove(move: suspend () -> Brick): TestAlgorithm {
        this.moves += move
        return this
    }

    fun defaultMove(move: suspend () -> Brick): TestAlgorithm {
        this.defaultMove = { move() }
        return this
    }

    override suspend fun initialize(gameInitialized: GameInitialized) = coroutineScope {
        delay(initTime)
    }

    override suspend fun move(last: MoveTrigger): Brick = when {
        moves.iterator().hasNext() -> moves.iterator().next()()
        else -> defaultMove()
    }

    override val identity: Identity = Identity(name)
}

