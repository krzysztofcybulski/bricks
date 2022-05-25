package me.kcybulski.bricks.server

import io.kotest.assertions.timing.eventually
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldStartWith
import me.kcybulski.bricks.server.utils.ResponseAssertions.asList
import me.kcybulski.bricks.server.utils.ResponseAssertions.asMap
import me.kcybulski.bricks.server.utils.TestServer
import me.kcybulski.bricks.server.utils.setupServer
import ratpack.http.client.ReceivedResponse
import kotlin.time.Duration.Companion.seconds

class LobbiesSpec : ShouldSpec({

    should("add lobby with random name") {
        //given
        val server = setupServer {}

        //when
        server.post("lobbies")

        //then
        eventually(5.seconds) {
            val response = server.get("lobbies")
            response.asList().first().run {
                get("name") shouldBe "lobby-0"
                get("status") shouldBe "OPEN"
                get("image") as String shouldStartWith "https"
                get("playersCount") shouldBe 0
                get("gamesCount") shouldBe 0
            }
        }
    }

    should("add lobby with given name normalized") {
        //given
        val server = setupServer {}

        //when
        server.post("lobbies", mapOf("name" to "Hello"))

        //then
        eventually(5.seconds) {
            val response = server.get("lobbies")
            response.first()["name"] shouldBe "hello"
        }
    }

    should("join lobby with websocket connection") {
        //given
        val server = setupServer {}
        val lobbyId = server.createLobby()

        //when
        server.websocket(
            "/lobbies/${lobbyId}/game",
            "key" to server.createApiKey()
        )

        //then
        eventually(5.seconds) {
            val response = server.get("/lobbies/${lobbyId}").asMap()
            val players = response["players"] as List<*>
            players.first().run {
                this as Map<String, Any>
                get("name") shouldBe "Anonymous"
                get("points") shouldBe 0
                get("avatarUrl") as String shouldStartWith "https"
            }
        }
    }
})

private fun ReceivedResponse.first() = asList().first()

private suspend fun TestServer.createLobby(): String {
    post("/lobbies")
    eventually(5.seconds) { get("/lobbies").asList().isNotEmpty() }
    return get("/lobbies").asList().first()["id"] as String
}

private fun TestServer.createApiKey() =
    post("/keys").asMap()["raw"] as String
