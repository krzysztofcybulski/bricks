package me.kcybulski.bricks.server

import me.kcybulski.bricks.server.utils.BaseSpec
import me.kcybulski.bricks.server.utils.ResponseAssertions.emptyList
import me.kcybulski.bricks.server.utils.ResponseAssertions.emptyObject
import me.kcybulski.bricks.server.utils.ResponseAssertions.jsonShouldBe
import me.kcybulski.bricks.server.utils.should

class LobbiesSpec : BaseSpec({

    should("add new lobby") { app, _ ->
        app.httpClient.post("lobbies")

        //then
        app.httpClient.get("lobbies") jsonShouldBe listOf(
            mapOf(
                "name" to "lobby-0",
                "status" to "OPEN",
                "playerNames" to emptyList,
                "games" to emptyList,
                "points" to emptyObject
            )
        )
    }

})
