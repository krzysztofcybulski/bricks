package me.kcybulski.bricks.server.api

import kotlinx.coroutines.CoroutineScope
import me.kcybulski.bricks.gamehistory.GameEventsRenderer
import me.kcybulski.bricks.gamehistory.GameHistoriesFacade
import me.kcybulski.bricks.gamehistory.GameMapRenderer
import me.kcybulski.bricks.server.lobby.Entrance
import me.kcybulski.bricks.server.lobby.Lobby
import me.kcybulski.bricks.server.lobby.OpenLobby
import me.kcybulski.bricks.tournament.TournamentFacade
import ratpack.func.Action
import ratpack.handling.Chain
import ratpack.handling.Context
import ratpack.jackson.Jackson.fromJson
import ratpack.jackson.Jackson.json
import ratpack.server.RatpackServer
import ratpack.websocket.WebSockets
import java.util.UUID

class Server(
    private val entrance: Entrance,
    private val tournaments: TournamentFacade,
    private val gameHistories: GameHistoriesFacade,
    private val corsConfiguration: CorsConfiguration,
    private val coroutine: CoroutineScope
) {

    private val ratpackServer: RatpackServer = RatpackServer.of { server ->
        server
            .serverConfig { config ->
                config.threads(1)
            }
            .handlers(api())
    }

    fun start() = ratpackServer.start()

    fun stop() = ratpackServer.stop()

    private fun api(): Action<Chain> = Action { chain: Chain ->
        chain
            .all(corsConfiguration::addCORSHeaders)
            .path { ctx ->
                ctx.byMethod { method ->
                    method
                        .get { _ ->
                            entrance.lobbies()
                                .map(Lobby::toResponse)
                                .let { ctx.render(json(it)) }
                        }
                        .post { _ ->
                            entrance.newLobby().let {
                                ctx.render(json(it))
                            }
                        }
                }
            }
            .get(":lobby") { ctx ->
                entrance.lobby(ctx) { lobby ->
                    lobby.toResponse()
                        .let { ctx.render(json(it)) }
                }
            }
            .get(":lobby/game") { ctx ->
                entrance.lobby(ctx) { lobby ->
                    when (lobby) {
                        is OpenLobby -> WebSockets.websocket(ctx, WSHandler(lobby, coroutine))
                        else -> ctx.response.status(400)
                    }
                }
            }
            .post(":lobby/start") { ctx ->
                entrance.lobby(ctx) { lobby ->
                    ctx.parse(fromJson(StartRequest::class.java))
                        .map { entrance.start(lobby.name, tournaments, it.toSettings()) }
                        .map { lobby.toResponse() }
                        .then { ctx.render(json(it)) }
                }
            }
            .get("games/:gameId/events") { ctx ->
                gameHistories.game(ctx.gameId)
                    .getAllEvents()
                    .map(GameEventsRenderer::toEventResponse)
                    .let(::json)
                    .let(ctx::render)
            }
            .get("games/:gameId/:time?") { ctx ->
                gameHistories.game(ctx.gameId)
                    .at(ctx.gameTime)
                    ?.let(GameMapRenderer::toString)
                    ?.let(ctx::render)
                    ?: ctx.notFound()
            }
    }

}

private fun Entrance.lobby(ctx: Context, handler: (Lobby) -> Unit) = get(ctx.pathTokens["lobby"]!!)
    ?.let { handler(it) }
    ?: ctx.notFound()

private val Context.gameId get() = UUID.fromString(pathTokens["gameId"]!!)

private val Context.gameTime get() = pathTokens["time"]?.toInt() ?: 10000
