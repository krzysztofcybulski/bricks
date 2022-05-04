package me.kcybulski.bricks.server.api.games

import me.kcybulski.bricks.gamehistory.GameEventsRenderer
import me.kcybulski.bricks.gamehistory.GameHistoriesFacade
import me.kcybulski.bricks.gamehistory.GameMapRenderer
import ratpack.handling.Chain
import ratpack.handling.Context
import ratpack.jackson.Jackson
import java.util.UUID

class GamesApi(
    private val gameHistories: GameHistoriesFacade
) {

    fun api(chain: Chain) {
        chain.prefix(":gameId", this::singleGameApi)
    }

    fun singleGameApi(chain: Chain) {
        chain
            .get("events") { ctx ->
                gameHistories.game(ctx.gameId)
                    .getAllEvents()
                    .map(GameEventsRenderer::toEventResponse)
                    .let(Jackson::json)
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

}

private val Context.gameId get() = UUID.fromString(allPathTokens["gameId"]!!)

private val Context.gameTime get() = pathTokens["time"]?.toInt() ?: 10000