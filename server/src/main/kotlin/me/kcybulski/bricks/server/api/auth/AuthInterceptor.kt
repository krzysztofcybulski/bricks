package me.kcybulski.bricks.server.api.auth

import mu.KotlinLogging
import ratpack.handling.Context
import ratpack.registry.Registry
import java.lang.System.getenv

class AuthInterceptor(
    private val verifier: TokenVerifier
) {

    fun intercept(ctx: Context) {
        when(val result = verifier.verify(ctx)) {
            NotVerified -> ctx.next()
            is Verified -> ctx.next(Registry.single(result))
        }
    }

    companion object {

        private val logger = KotlinLogging.logger {}

        fun fromEnvironmentVariables(): AuthInterceptor {
            val domain = getenv()["AUTH_DOMAIN"]
            val keyId = getenv()["AUTH_KEY_ID"]
            val audience = getenv()["AUTH_AUDIENCE"]
            return if(domain != null && keyId != null && audience != null) {
                logger.warn { "Using JWT authentication $audience" }
                AuthInterceptor(JWTVerifier(domain, keyId, audience))
            } else {
                logger.warn { "Using no authentication" }
                AuthInterceptor(NoVerifier)
            }
        }

    }
}

fun authenticated(ctx: Context, handler: (Verified) -> Unit) = ctx.find<Verified>()
    ?.let { handler(it) }
    ?: run { ctx.clientError(401) }

private inline fun <reified O> Context.find(): O? = maybeGet(O::class.java)
    .orElse(null)