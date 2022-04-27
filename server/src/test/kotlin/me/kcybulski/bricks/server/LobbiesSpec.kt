package me.kcybulski.bricks.server

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import me.kcybulski.bricks.server.utils.ResponseAssertions.asList
import me.kcybulski.bricks.server.utils.ResponseAssertions.emptyList
import me.kcybulski.bricks.server.utils.ResponseAssertions.emptyObject
import me.kcybulski.bricks.server.utils.setupServer
import ratpack.http.client.ReceivedResponse

class LobbiesSpec : ShouldSpec({

    should("add lobby with random name") {
        //given
        val server = setupServer {}

        //when
        server.post("lobbies")

        //then
        val response = server.get("lobbies")
        response.asList() shouldBe listOf(
            mapOf(
                "name" to "lobby-0",
                "status" to "OPEN",
                "playerNames" to emptyList,
                "games" to emptyList,
                "points" to emptyObject
            )
        )
    }

    should("add lobby with given name") {
        //given
        val server = setupServer {
        }

        //when
        server.post("lobbies", mapOf("name" to "Hello"))

        //then
        val response = server.get("lobbies")
        response.first()["name"] shouldBe "Hello"
    }
})

private fun ReceivedResponse.first() = asList().first()
