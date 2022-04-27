package me.kcybulski.bricks.server

import me.kcybulski.bricks.server.utils.BaseSpec
import me.kcybulski.bricks.server.utils.ResponseAssertions.jsonShouldBe
import me.kcybulski.bricks.server.utils.should

class BotsSpec : BaseSpec({

    should("list all available bots") { app, _ ->
        //expect
        app.httpClient.get("bots") jsonShouldBe listOf(
            mapOf(
                "name" to "Alpha"
            )
        )
    }

})
