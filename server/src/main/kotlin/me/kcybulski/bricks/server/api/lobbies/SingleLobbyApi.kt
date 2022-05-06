package me.kcybulski.bricks.server.api.lobbies

import kotlinx.coroutines.CoroutineScope
import me.kcybulski.bricks.auth.ApiKeys
import me.kcybulski.bricks.bots.Bots
import me.kcybulski.bricks.events.CommandBus
import me.kcybulski.bricks.lobbies.JoinLobbyCommand
import me.kcybulski.bricks.lobbies.LobbyId
import me.kcybulski.bricks.lobbies.StartTournamentCommand
import me.kcybulski.bricks.server.api.apikeys.apiAuthenticated
import me.kcybulski.bricks.server.api.auth.authenticated
import me.kcybulski.bricks.server.api.renderJson
import me.kcybulski.bricks.server.views.lobbies.LobbyDetailsReadModel
import me.kcybulski.bricks.server.views.lobbies.LobbyDetailsView
import me.kcybulski.bricks.server.views.lobbies.LobbyDetailsView.Status.OPEN
import ratpack.handling.Chain
import ratpack.handling.Context
import ratpack.jackson.Jackson
import ratpack.websocket.WebSockets
import java.util.UUID

class SingleLobbyApi(
    private val lobbyReadModel: LobbyDetailsReadModel,
    private val bots: Bots,
    private val apiKeys: ApiKeys,
    private val websocketsRegistry: WebsocketsRegistry,
    private val commandBus: CommandBus,
    private val coroutine: CoroutineScope
) {

    fun api(chain: Chain) {
        chain
            .get { ctx ->
                lobby(ctx) { it.renderJson(ctx) }
            }
            .get("game") { ctx ->
                apiAuthenticated(apiKeys, ctx) { apiUser ->
                    openLobby(ctx) { lobby ->
                        WebSockets.websocket(ctx, WSHandler(LobbyId(lobby.id), websocketsRegistry, apiUser, coroutine))
                    }
                }
            }
            .post("tournaments") { ctx ->
                authenticated(ctx) {
                    lobby(ctx) { lobby ->
                        ctx
                            .parse(Jackson.fromJson(StartRequest::class.java))
                            .map { commandBus.send(StartTournamentCommand(LobbyId(lobby.id), it.toSettings())) }
                            .then { ctx.response.status(201).send() }
                    }
                }
            }
            .post("bots") { ctx ->
                authenticated(ctx) {
                    openLobby(ctx) { lobby ->
                        ctx
                            .parse(Jackson.fromJson(AddBotRequest::class.java))
                            .map { bots.getAlgorithm(it.name) }
                            .map { algorithm ->
                                algorithm?.let {
                                    commandBus.send(JoinLobbyCommand(LobbyId(lobby.id), it))
                                }
                            }
                            .then { ctx.response.status(201).send() }
                    }
                }
            }
    }

    private fun lobby(ctx: Context, handler: (LobbyDetailsView) -> Unit) =
        LobbyId(UUID.fromString(ctx.allPathTokens["lobby"]!!))
            .let { lobbyReadModel.findLobby(it) }
            ?.let { handler(it) }
            ?: ctx.notFound()

    private fun openLobby(ctx: Context, handler: (LobbyDetailsView) -> Unit) =
        lobby(ctx) {
            when (it.status) {
                OPEN -> handler(it)
                else -> ctx.response.status(400)
            }
        }
}
