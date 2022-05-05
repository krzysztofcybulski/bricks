package me.kcybulski.bricks.server.api.lobbies

import me.kcybulski.bricks.events.CommandBus
import me.kcybulski.bricks.lobbies.CreateLobbyCommand
import me.kcybulski.bricks.server.api.auth.authenticated
import me.kcybulski.bricks.server.api.renderJson
import me.kcybulski.bricks.server.healthcheck.RefreshLobbies
import me.kcybulski.bricks.server.views.lobbies.LobbiesListReadModel
import ratpack.handling.Chain
import ratpack.jackson.Jackson.fromJson
import ratpack.websocket.WebSockets

class LobbiesListApiV2(
    private val lobbiesView: LobbiesListReadModel,
    private val refreshLobbies: RefreshLobbies,
    private val singleLobbyApi: SingleLobbyApiV2,
    private val commandBus: CommandBus
) {

    fun api(chain: Chain) {
        chain
            .path { ctx ->
                ctx.byMethod { m ->
                    m.get { c ->
                        lobbiesView
                            .findAllLobbies()
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