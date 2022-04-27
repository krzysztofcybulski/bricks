package me.kcybulski.bricks.server

import io.kotest.common.ExperimentalKotest
import io.kotest.core.spec.IsolationMode.InstancePerTest
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import me.kcybulski.bricks.server.utils.ResponseAssertions.emptyList
import me.kcybulski.bricks.server.utils.ResponseAssertions.emptyObject
import me.kcybulski.bricks.server.utils.TestConfigurations.list
import me.kcybulski.bricks.server.utils.should

@ExperimentalKotest
class LobbiesSpec : ShouldSpec({

    isolationMode = InstancePerTest
    testCoroutineDispatcher = true

    should("add new lobby") { app, _ ->
        //given
        app.httpClient.post("lobbies")

        //when
        val response = app.httpClient.get("lobbies")

        //then
        list(response) shouldBe listOf(
            mapOf(
                "name" to "0",
                "status" to "OPEN",
                "playerNames" to emptyList,
                "games" to emptyList,
                "points" to emptyObject
            )
        )
    }

})
