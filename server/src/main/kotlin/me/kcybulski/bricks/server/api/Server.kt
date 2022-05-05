package me.kcybulski.bricks.server.api

import me.kcybulski.bricks.server.ErrorHandler
import me.kcybulski.bricks.server.api.apikeys.ApiKeysApi
import me.kcybulski.bricks.server.api.auth.AuthInterceptor
import me.kcybulski.bricks.server.api.bots.BotsApi
import me.kcybulski.bricks.server.api.games.GamesApi
import me.kcybulski.bricks.server.api.lobbies.LobbiesListApi
import ratpack.error.ClientErrorHandler
import ratpack.error.ServerErrorHandler
import ratpack.handling.Chain
import ratpack.handling.Context
import ratpack.jackson.Jackson.json
import ratpack.registry.Registry
import ratpack.server.RatpackServer

class Server(
    private val lobbiesApi: LobbiesListApi,
    private val gamesApi: GamesApi,
    private val botsApi: BotsApi,
    private val apiKeysApi: ApiKeysApi,
    private val corsConfiguration: CorsConfiguration,
    private val authInterceptor: AuthInterceptor,
    private val port: Int? = null
) {

    val ratpackServer: RatpackServer = RatpackServer.of { server ->
        server
            .serverConfig { config ->
                config
                    .env()
                    .port(port ?: 5050)
                    .threads(1)
            }
            .registry(Registry.of { registry ->
                registry.add(ClientErrorHandler::class.java, ErrorHandler())
                registry.add(ServerErrorHandler::class.java, ErrorHandler())
            })
            .handlers { chain: Chain ->
                chain
                    .all(corsConfiguration::addCORSHeaders)
                    .all(authInterceptor::intercept)
                    .prefix("lobbies", lobbiesApi::api)
                    .prefix("games", gamesApi::api)
                    .prefix("bots", botsApi::api)
                    .prefix("keys", apiKeysApi::api)
            }
    }

    fun start() = ratpackServer.start()

}

fun Any.renderJson(ctx: Context) = ctx.render(json(this))