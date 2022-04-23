package me.kcybulski.bricks.server.api

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import me.kcybulski.bricks.bots.Bots
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
    private val bots: Bots,
    private val corsConfiguration: CorsConfiguration,
    private val coroutineScope: CoroutineScope,
    private val port: Int? = null
) {

    private val ratpackServer: RatpackServer = RatpackServer.of { server ->
        server
            .serverConfig { config ->
                config
                    .port(port ?: 5050)
                    .threads(1)
            }
            .handlers(api())
    }

    fun start() = ratpackServer.start()

    fun stop() = ratpackServer.stop()

    private fun api(): Action<Chain> = Action { chain: Chain ->
        chain
            .all(corsConfiguration::addCORSHeaders)
            .prefix("lobbies") { lobbiesChain ->
                lobbiesChain
                    .get { ctx ->
                        entrance.lobbies()
                            .map { it.toResponse(gameHistories) }
                            .let { ctx.render(json(it)) }
                    }
                    .post { ctx ->
                        entrance.newLobby().let {
                            ctx.render(json(it))
                        }
                    }
                    .prefix(":lobby") { lobbyChain ->
                        lobbyChain
                            .get { ctx ->
                                entrance.lobby(ctx) { lobby ->
                                    lobby.toResponse(gameHistories)
                                        .renderJson(ctx)
                                }
                            }
                            .get("game") { ctx ->
                                entrance.lobby(ctx) { lobby ->
                                    when (lobby) {
                                        is OpenLobby -> WebSockets.websocket(ctx, WSHandler(lobby, coroutineScope))
                                        else -> ctx.response.status(400)
                                    }
                                }
                            }
                            .post("tournaments") { ctx ->
                                entrance.lobby(ctx) { lobby ->
                                    ctx.parse(fromJson(StartRequest::class.java))
                                        .map {
                                            coroutineScope.launch {
                                                entrance.start(lobby.name, tournaments, it.toSettings())
                                            }
                                        }
                                        .map { lobby.toResponse(gameHistories) }
                                        .then { ctx.render(json(it)) }
                                }
                            }
                            .post("bots") { ctx ->
                                entrance.lobby(ctx) { lobby ->
                                    when (lobby) {
                                        is OpenLobby -> ctx
                                            .parse(fromJson(AddBotRequest::class.java))
                                            .map { bots.getAlgorithm(it.name) }
                                            .map { algorithm -> algorithm?.let { lobby.registerBot(it) } }
                                            .map { lobby.toResponse(gameHistories) }
                                            .then { ctx.render(json(it)) }
                                        else -> ctx.response.status(400)
                                    }
                                }
                            }
                    }
            }
            .prefix("games/:gameId") { gameChain ->
                gameChain
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
            .get("bots") { ctx ->
                bots.getBotNames()
                    .map(::BotResponse)
                    .let(::json)
                    .let(ctx::render)
            }
    }

}

private fun Entrance.lobby(ctx: Context, handler: (Lobby) -> Unit) = get(ctx.pathTokens["lobby"]!!)
    ?.let { handler(it) }
    ?: ctx.notFound()

private val Context.gameId get() = UUID.fromString(pathTokens["gameId"]!!)

private val Context.gameTime get() = pathTokens["time"]?.toInt() ?: 10000

private fun Any.renderJson(ctx: Context) = ctx.render(json(this))
