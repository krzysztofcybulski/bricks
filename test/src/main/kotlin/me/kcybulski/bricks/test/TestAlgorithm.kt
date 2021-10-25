package me.kcybulski.bricks.test

import arrow.core.Either
import arrow.core.getOrElse
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import me.kcybulski.bricks.game.Algorithm
import me.kcybulski.bricks.game.Brick
import me.kcybulski.bricks.game.Identity
import me.kcybulski.bricks.game.MoveTrigger
import me.kcybulski.bricks.game.NewGame

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

    fun nextMoveEither(move: suspend () -> Either<Any, Brick>): TestAlgorithm {
        this.moves += { move().getOrElse { throw IllegalArgumentException("Invalid brick") } }
        return this
    }

    fun defaultMoveEither(move: suspend () -> Either<Any, Brick>): TestAlgorithm {
        this.defaultMove = { move().getOrElse { throw IllegalArgumentException("Invalid brick") } }
        return this
    }

    override suspend fun initialize(game: NewGame) = coroutineScope {
        delay(initTime)
    }

    override suspend fun move(last: MoveTrigger): Brick = when {
        moves.iterator().hasNext() -> moves.iterator().next()()
        else -> defaultMove()
    }

    override val identity: Identity = Identity(name)
}

