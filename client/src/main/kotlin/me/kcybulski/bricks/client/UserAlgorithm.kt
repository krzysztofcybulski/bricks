package me.kcybulski.bricks.client

import me.kcybulski.bricks.game.Algorithm
import me.kcybulski.bricks.game.Brick
import me.kcybulski.bricks.game.Identity
import me.kcybulski.bricks.game.MoveTrigger
import me.kcybulski.bricks.game.MoveTrigger.FirstMove
import me.kcybulski.bricks.game.MoveTrigger.OpponentMoved
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
