package me.kcybulski.bricks.server.api

import me.kcybulski.bricks.bots.Bots
import ratpack.handling.Chain
import ratpack.jackson.Jackson

class BotsApi(
    private val bots: Bots
) {

    fun api(chain: Chain) {
        chain
            .get { ctx ->
                bots.getBotNames()
                    .map(::BotResponse)
                    .let(Jackson::json)
                    .let(ctx::render)
            }
    }

}