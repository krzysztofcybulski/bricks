package me.kcybulski.bricks.client

import me.kcybulski.bricks.api.Algorithm
import me.kcybulski.bricks.api.Brick
import me.kcybulski.bricks.api.Identity
import me.kcybulski.bricks.api.MoveTrigger
import me.kcybulski.bricks.api.MoveTrigger.FirstMove
import me.kcybulski.bricks.api.MoveTrigger.OpponentMoved
import java.lang.System.getProperty

abstract class UserAlgorithm(
    suffix: String = ""
) : Algorithm {

    override val identity = Identity("${getProperty("user.name")} $suffix".trim())

    override suspend fun move(last: MoveTrigger): Brick =
        when (last) {
            FirstMove -> firstMove()
            is OpponentMoved -> move(last)
        }

    abstract suspend fun firstMove(): Brick

    abstract suspend fun move(opponentMoved: OpponentMoved): Brick

}
