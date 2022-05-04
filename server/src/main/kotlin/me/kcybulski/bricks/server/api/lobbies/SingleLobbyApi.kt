package me.kcybulski.bricks.server.api.lobbies

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import me.kcybulski.bricks.auth.ApiKeys
import me.kcybulski.bricks.bots.Bots
import me.kcybulski.bricks.gamehistory.GameHistoriesFacade
import me.kcybulski.bricks.server.api.WSHandler
import me.kcybulski.bricks.server.api.apikeys.apiAuthenticated
import me.kcybulski.bricks.server.api.auth.authenticated
import me.kcybulski.bricks.server.api.renderJson
import me.kcybulski.bricks.server.api.toResponse
import me.kcybulski.bricks.server.lobby.Entrance
import me.kcybulski.bricks.server.lobby.Lobby
import me.kcybulski.bricks.server.lobby.OpenLobby
import me.kcybulski.bricks.tournament.TournamentFacade
import ratpack.handling.Chain
import ratpack.handling.Context
import ratpack.jackson.Jackson
import ratpack.websocket.WebSockets

class LobbyApi(
    private val gameHistories: GameHistoriesFacade,
    private val entrance: Entrance,
    private val tournaments: TournamentFacade,
    private val bots: Bots,
    private val apiKeys: ApiKeys,
    private val coroutine: CoroutineScope
) {

    fun api(chain: Chain) {
        chain
            .get { ctx ->
                entrance.lobby(ctx) { lobby ->
                    lobby.toResponse(gameHistories)
                        .renderJson(ctx)
                }
            }
            .get("game") { ctx ->
                entrance.lobby(ctx) { lobby ->
                    apiAuthenticated(apiKeys, ctx) { apiUser ->
                        when (lobby) {
                            is OpenLobby -> WebSockets.websocket(ctx, WSHandler(lobby, apiUser, coroutine))
                            else -> ctx.response.status(400)
                        }
                    }
                }
            }
            .post("tournaments") { ctx ->
                authenticated(ctx) {
                    entrance.lobby(ctx) { lobby ->
                        ctx.parse(Jackson.fromJson(StartRequest::class.java))
                            .map {
                                coroutine.launch {
                                    entrance.start(lobby.name, tournaments, it.toSettings())
                                }
                            }
                            .map { lobby.toResponse(gameHistories) }
                            .then { it.renderJson(ctx) }
                    }
                }
            }
            .post("bots") { ctx ->
                authenticated(ctx) {
                    entrance.lobby(ctx) { lobby ->
                        when (lobby) {
                            is OpenLobby -> ctx
                                .parse(Jackson.fromJson(AddBotRequest::class.java))
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
}

private fun Entrance.lobby(ctx: Context, handler: (Lobby) -> Unit) = get(ctx.allPathTokens["lobby"]!!)
    ?.let { handler(it) }
    ?: ctx.notFound()
