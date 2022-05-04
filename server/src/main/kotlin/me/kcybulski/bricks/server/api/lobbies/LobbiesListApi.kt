package me.kcybulski.bricks.server.api.lobbies

import me.kcybulski.bricks.events.CommandBus
import me.kcybulski.bricks.gamehistory.GameHistoriesFacade
import me.kcybulski.bricks.lobbies.CreateLobbyCommand
import me.kcybulski.bricks.lobbies.SimpleLobbiesView
import me.kcybulski.bricks.server.api.auth.authenticated
import me.kcybulski.bricks.server.api.renderJson
import me.kcybulski.bricks.server.api.toResponse
import me.kcybulski.bricks.server.lobby.RefreshLobbies
import ratpack.handling.Chain
import ratpack.jackson.Jackson.fromJson
import ratpack.websocket.WebSockets

class LobbiesListApi(
    private val gameHistories: GameHistoriesFacade,
    private val lobbiesView: SimpleLobbiesView,
    private val refreshLobbies: RefreshLobbies,
    private val singleLobbyApi: LobbyApi,
    private val commandBus: CommandBus
) {

    fun api(chain: Chain) {
        chain
            .path { ctx ->
                ctx.byMethod { m ->
                    m.get { c ->
                        lobbiesView
                            .findAllLobbies()
                            .map { it.toResponse(gameHistories) }
                            .renderJson(c)
                    }
                        .post { c ->
                            authenticated(c) {
                                ctx.parse(fromJson(AddLobbyRequest::class.java))
                                    .map { commandBus.send(CreateLobbyCommand(it.name)) }
                                    .then { ctx.response.status(201).send() }
                            }
                        }
                }
            }
            .get("updates") { ctx ->
                WebSockets.websocket(ctx, refreshLobbies)
            }
            .prefix(":lobby", singleLobbyApi::api)

    }
}