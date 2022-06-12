package me.kcybulski.bricks.server.api.auth

import ratpack.handling.Context

interface TokenVerifier {

    fun verify(ctx: Context): VerificationResult

}

sealed class VerificationResult

object NotVerified : VerificationResult()

data class Verified(
    val id: String,
    val name: String,
    val avatar: String
) : VerificationResult()
