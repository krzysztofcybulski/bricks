package me.kcybulski.bricks.client

import me.kcybulski.bricks.api.Algorithm
import me.kcybulski.bricks.api.Identity
import java.lang.System.getProperty

abstract class UserAlgorithm(
    suffix: String = ""
) : Algorithm {

    override val identity = Identity("${getProperty("user.name")} $suffix".trim())

}
