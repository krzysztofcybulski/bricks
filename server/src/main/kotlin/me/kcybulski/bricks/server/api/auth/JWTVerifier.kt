package me.kcybulski.bricks.server.api.auth

import com.auth0.jwk.UrlJwkProvider
import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTVerificationException
import com.auth0.jwt.interfaces.DecodedJWT
import com.google.common.net.HttpHeaders.AUTHORIZATION
import ratpack.handling.Context
import java.security.interfaces.RSAPublicKey

private const val NICKNAME_CLAIM = "https://bricks.kcybulski.me/nickname"

class JWTVerifier(
    domain: String,
    keyId: String,
    audience: String
) : TokenVerifier {

    private val algorithm: Algorithm = Algorithm.RSA256(
        UrlJwkProvider(domain)[keyId].publicKey as RSAPublicKey,
        null
    )

    private val verifier: JWTVerifier = JWT.require(algorithm)
        .withAudience(audience)
        .build()

    override fun verify(ctx: Context): VerificationResult =
        ctx
            .getAuthorizationToken()
            ?.let(::verify)
            ?: NotVerified

    private fun verify(token: String): VerificationResult =
        try {
            val jwt: DecodedJWT = verifier.verify(token)
            Verified(jwt.subject, jwt.claims[NICKNAME_CLAIM]?.asString() ?: jwt.subject)
        } catch (exception: JWTVerificationException) {
            NotVerified
        }

}

private fun Context.getAuthorizationToken() =
    header(AUTHORIZATION)
        .map { it.removePrefix("Bearer ") }
        .orElse(null)
