package me.kcybulski.bricks.server.api.auth

import ratpack.handling.Context

object NoVerifier : TokenVerifier {

    override fun verify(ctx: Context) =
        Verified("anonymous", "Anonymous")

}