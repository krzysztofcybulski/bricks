package me.kcybulski.bricks.server.api

import me.kcybulski.bricks.gamehistory.GameHistoriesFacade
import me.kcybulski.bricks.gamehistory.GameMapRenderer
import me.kcybulski.bricks.server.lobby.Entrance
import me.kcybulski.bricks.server.lobby.Lobby
import me.kcybulski.bricks.server.lobby.OpenLobby
import me.kcybulski.bricks.tournament.TournamentFacade
import ratpack.func.Action
import ratpack.handling.Chain
import ratpack.handling.Context
import ratpack.jackson.Jackson
import ratpack.server.RatpackServer
import ratpack.websocket.WebSockets
import java.util.UUID

class Server(
    private val entrance: Entrance,
    private val tournaments: TournamentFacade,
    private val gameHistories: GameHistoriesFacade
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
            .path { ctx ->
                ctx.byMethod { method ->
                    method
                        .get { _ ->
                            entrance.lobbies()
                                .map(Lobby::toResponse)
                                .let { ctx.render(Jackson.json(it)) }
                        }
                        .post { _ ->
                            entrance.newLobby().let {
                                ctx.render(Jackson.json(it))
                            }
                        }
                }
            }
            .get(":lobby") { ctx ->
                entrance.lobby(ctx) { lobby ->
                    lobby.toResultsResponse()
                        .let { ctx.render(Jackson.json(it)) }
                }
            }
            .get(":lobby/game") { ctx ->
                entrance.lobby(ctx) {
                    when (it) {
                        is OpenLobby -> WebSockets.websocket(ctx, WSHandler(it))
                        else -> ctx.response.status(400)
                    }
                }
            }
            .post(":lobby/start") { ctx ->
                entrance.lobby(ctx) { lobby ->
                    entrance.start(lobby.name, tournaments)
                    lobby.toResultsResponse()
                        .let { ctx.render(Jackson.json(it)) }
                }
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
