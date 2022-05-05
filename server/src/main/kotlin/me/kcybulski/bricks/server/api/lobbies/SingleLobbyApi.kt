package me.kcybulski.bricks.server.api.lobbies

import kotlinx.coroutines.CoroutineScope
import me.kcybulski.bricks.auth.ApiKeys
import me.kcybulski.bricks.bots.Bots
import me.kcybulski.bricks.events.CommandBus
import me.kcybulski.bricks.gamehistory.GameHistoriesFacade
import me.kcybulski.bricks.lobbies.JoinLobbyCommand
import me.kcybulski.bricks.lobbies.LobbyId
import me.kcybulski.bricks.lobbies.StartTournamentCommand
import me.kcybulski.bricks.server.api.WSHandler
import me.kcybulski.bricks.server.api.WebsocketsRegistry
import me.kcybulski.bricks.server.api.apikeys.apiAuthenticated
import me.kcybulski.bricks.server.api.auth.authenticated
import me.kcybulski.bricks.server.api.renderJson
import me.kcybulski.bricks.server.api.toResponse
import me.kcybulski.bricks.server.views.lobbies.LobbiesListReadModel
import me.kcybulski.bricks.server.views.lobbies.LobbyView
import me.kcybulski.bricks.server.views.lobbies.LobbyView.Status.OPEN
import ratpack.handling.Chain
import ratpack.handling.Context
import ratpack.jackson.Jackson
import ratpack.websocket.WebSockets

class LobbyApi(
    private val gameHistories: GameHistoriesFacade,
    private val lobbiesView: LobbiesListReadModel,
    private val bots: Bots,
    private val apiKeys: ApiKeys,
    private val websocketsRegistry: WebsocketsRegistry,
    private val commandBus: CommandBus,
    private val coroutine: CoroutineScope
) {

    fun api(chain: Chain) {
        chain
            .get { ctx ->
                lobbiesView.lobby(ctx) { lobby ->
                    lobby.toResponse(gameHistories)
                        .renderJson(ctx)
                }
            }
            .get("game") { ctx ->
                lobbiesView.lobby(ctx) { lobby ->
                    apiAuthenticated(apiKeys, ctx) { apiUser ->
                        when (lobby.status) {
                            OPEN -> WebSockets.websocket(ctx, WSHandler(LobbyId(lobby.id), websocketsRegistry, apiUser, coroutine))
                            else -> ctx.response.status(400)
                        }
                    }
                }
            }
            .post("tournaments") { ctx ->
                authenticated(ctx) {
                    lobbiesView.lobby(ctx) { lobby ->
                        ctx
                            .parse(Jackson.fromJson(StartRequest::class.java))
                            .map { commandBus.send(StartTournamentCommand(LobbyId(lobby.id), it.toSettings())) }
                            .then { ctx.response.status(201).send() }
                    }
                }
            }
            .post("bots") { ctx ->
                authenticated(ctx) {
                    lobbiesView.lobby(ctx) { lobby ->
                        when (lobby.status) {
                            OPEN -> ctx
                                .parse(Jackson.fromJson(AddBotRequest::class.java))
                                .map { bots.getAlgorithm(it.name) }
                                .map { algorithm ->
                                    algorithm?.let {
                                        commandBus.send(JoinLobbyCommand(LobbyId(lobby.id), it))
                                    }
                                }
                                .then { ctx.response.status(201).send() }
                            else -> ctx.response.status(400)
                        }
                    }
                }
            }
    }
}

private fun LobbiesListReadModel.lobby(ctx: Context, handler: (LobbyView) -> Unit) =
    findLobby(ctx.allPathTokens["lobby"]!!)
        ?.let { handler(it) }
        ?: ctx.notFound()
