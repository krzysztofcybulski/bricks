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
import me.kcybulski.bricks.server.lobby.RefreshLobbies
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
    private val refreshLobbies: RefreshLobbies,
    private val port: Int? = null
) {

    val ratpackServer: RatpackServer = RatpackServer.of { server ->
        server
            .serverConfig { config ->
                config
                    .port(port ?: 5050)
                    .threads(1)
            }
            .handlers(api())
    }

    fun start() = ratpackServer.start()

    private fun api(): Action<Chain> = Action { chain: Chain ->
        chain
            .all(corsConfiguration::addCORSHeaders)
            .prefix("lobbies") { lobbiesApi(it) }
            .prefix("games/:gameId") { gameChain ->
                gameChain
                    .get("events") { ctx ->
                        gameHistories.game(ctx.gameId)
                            .getAllEvents()
                            .map(GameEventsRenderer::toEventResponse)
                            .let(::json)
                            .let(ctx::render)
                    }
                    .get(":time?") { ctx ->
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
            .let { lobbiesApi(it) }
    }

    private fun lobbiesApi(lobbiesChain: Chain) =
        lobbiesChain
            .path { ctx ->
                ctx.byMethod { m ->
                    m.get { c ->
                        entrance.lobbies()
                            .map { it.toResponse(gameHistories) }
                            .renderJson(c)
                    }
                        .post { c -> entrance.newLobby().renderJson(c) }
                }
            }
            .get("updates") { ctx ->
                WebSockets.websocket(ctx, refreshLobbies)
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
                                .then { it.renderJson(ctx) }
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
                                    .then { it.renderJson(ctx) }
                                else -> ctx.response.status(400)
                            }
                        }
                    }
            }

}

private fun Entrance.lobby(ctx: Context, handler: (Lobby) -> Unit) = get(ctx.allPathTokens["lobby"]!!)
    ?.let { handler(it) }
    ?: ctx.notFound()

private val Context.gameId get() = UUID.fromString(allPathTokens["gameId"]!!)

private val Context.gameTime get() = pathTokens["time"]?.toInt() ?: 10000

private fun Any.renderJson(ctx: Context) = ctx.render(json(this))
