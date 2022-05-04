package me.kcybulski.bricks.server.api.apikeys

import me.kcybulski.bricks.auth.ApiKey
import me.kcybulski.bricks.auth.ApiKeys
import me.kcybulski.bricks.auth.ApiUser
import me.kcybulski.bricks.server.api.auth.authenticated
import me.kcybulski.bricks.server.api.renderJson
import ratpack.handling.Chain
import ratpack.handling.Context

class ApiKeysApi(
    private val apiKeys: ApiKeys
) {

    fun api(chain: Chain) {
        chain
            .post { ctx ->
                authenticated(ctx) { user ->
                    apiKeys.generateKey(ApiUser(user.id))
                        .let { ApiKeyResponse(it.raw) }
                        .renderJson(ctx)
                }
            }
    }

}

private data class ApiKeyResponse(
    val raw: String
)

fun apiAuthenticated(apiKeys: ApiKeys, ctx: Context, handler: (ApiUser) -> Unit) =
    ctx
        .request
        .queryParams["key"]
        ?.let { apiKeys.authorize(ApiKey(it)) }
        ?.let { handler(it) }
        ?: run { ctx.clientError(401) }