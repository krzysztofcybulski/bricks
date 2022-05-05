package me.kcybulski.bricks.server

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldStartWith
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
        response.asList().first().run {
            get("name") shouldBe "lobby-0"
            get("status") shouldBe "OPEN"
            get("image") as String shouldStartWith "https"
            get("players") shouldBe emptyList
            get("games") shouldBe emptyList
            get("points") shouldBe emptyObject
        }
    }

    should("add lobby with given name normalized") {
        //given
        val server = setupServer {
        }

        //when
        server.post("lobbies", mapOf("name" to "Hello"))

        //then
        val response = server.get("lobbies")
        response.first()["name"] shouldBe "hello"
    }
})

private fun ReceivedResponse.first() = asList().first()
