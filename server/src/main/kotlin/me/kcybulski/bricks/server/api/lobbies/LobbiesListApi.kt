package me.kcybulski.bricks.server.api.lobbies

import me.kcybulski.bricks.gamehistory.GameHistoriesFacade
import me.kcybulski.bricks.server.api.auth.authenticated
import me.kcybulski.bricks.server.api.renderJson
import me.kcybulski.bricks.server.api.toResponse
import me.kcybulski.bricks.server.lobby.Entrance
import me.kcybulski.bricks.server.lobby.RefreshLobbies
import ratpack.handling.Chain
import ratpack.jackson.Jackson.fromJson
import ratpack.websocket.WebSockets

class LobbiesListApi(
    private val gameHistories: GameHistoriesFacade,
    private val entrance: Entrance,
    private val refreshLobbies: RefreshLobbies,
    private val singleLobbyApi: LobbyApi
) {

    fun api(chain: Chain) {
        chain
            .path { ctx ->
                ctx.byMethod { m ->
                    m.get { c ->
                        entrance.lobbies()
                            .map { it.toResponse(gameHistories) }
                            .renderJson(c)
                    }
                        .post { c ->
                            authenticated(c) {
                                ctx.parse(fromJson(AddLobbyRequest::class.java))
                                    .map { entrance.newLobby(it.name) }
                                    .map { it.toResponse(gameHistories) }
                                    .then { it.renderJson(ctx) }
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