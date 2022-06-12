package me.kcybulski.bricks.server.api.auth

import me.kcybulski.bricks.api.Identity
import me.kcybulski.bricks.server.views.Avatars
import ratpack.handling.Context

object NoVerifier : TokenVerifier {

    override fun verify(ctx: Context) =
        Verified(
            id = "anonymous",
            name = "Anonymous",
            avatar = Avatars.generateForPlayer(Identity("anonym"))
        )
}
