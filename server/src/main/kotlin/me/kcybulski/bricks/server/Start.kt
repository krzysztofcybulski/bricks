package me.kcybulski.bricks.server

import me.kcybulski.bricks.events.EventBus
import me.kcybulski.bricks.tournament.TournamentFacade
import ratpack.handling.Chain
import ratpack.handling.Context
import ratpack.jackson.Jackson.json
import ratpack.server.RatpackServer
import ratpack.server.RatpackServerSpec
import ratpack.websocket.WebSockets.websocket

fun main() {
    val tournaments = TournamentFacade(EventBus())
    val entrance = Entrance(LobbyFactory())
    Healthchecker(entrance).start()
    entrance.newLobby()
    RatpackServer.start { server: RatpackServerSpec ->
        server
            .handlers { chain: Chain ->
                chain
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
                            lobby.toResultsResponse()
                                .let { ctx.render(json(it)) }
                        }
                    }
                    .get(":lobby/game") { ctx ->
                        entrance.lobby(ctx) {
                            when (it) {
                                is OpenLobby -> websocket(ctx, WSHandler(it))
                                else -> ctx.response.status(400)
                            }
                        }
                    }
                    .post(":lobby/start") { ctx ->
                        entrance.lobby(ctx) { lobby ->
                            entrance.start(lobby.name, tournaments)
                            lobby.toResultsResponse()
                                .let { ctx.render(json(it)) }
                        }
                    }
            }
    }
}

private fun Entrance.lobby(ctx: Context, handler: (Lobby) -> Unit) = get(ctx.pathTokens["lobby"]!!)
    ?.let { handler(it) }
    ?: ctx.notFound()
