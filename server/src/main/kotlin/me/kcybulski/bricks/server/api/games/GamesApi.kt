package me.kcybulski.bricks.server.api.games

import me.kcybulski.bricks.server.api.renderJson
import me.kcybulski.bricks.server.views.gamehistory.GamesHistoryReadModel
import ratpack.handling.Chain
import ratpack.handling.Context
import java.util.UUID

class GamesApi(
    private val gamesHistoryReadModel: GamesHistoryReadModel
) {

    fun api(chain: Chain) {
        chain.prefix(":gameId", this::singleGameApi)
    }

    private fun singleGameApi(chain: Chain) {
        chain
            .get { ctx ->
                gamesHistoryReadModel
                    .find(ctx.gameId)
                    ?.renderJson(ctx)
                    ?: ctx.notFound()

            }
    }

}

private val Context.gameId get() = UUID.fromString(allPathTokens["gameId"]!!)
