package me.kcybulski.bricks.server.utils

import io.kotest.core.spec.style.ShouldSpec
import kotlinx.coroutines.coroutineScope
import me.kcybulski.bricks.server.Configuration
import me.kcybulski.bricks.server.utils.ResponseAssertions.json
import ratpack.http.client.ReceivedResponse
import ratpack.test.embed.EmbeddedApp
import kotlin.random.Random

suspend fun server(spec: TestServerSpec.() -> Unit = {}) = coroutineScope {

    val settings = TestServerSpec().apply(spec)

    val configuration = Configuration.app(
        coroutine = this,
        lobbyNameGenerator = settings.lobbyNameGenerator,
        botNameGenerator = settings.botNameGenerator,
        serverPort = Random.nextInt(5000, 6000)
    )

    TestServer(configuration)
}

class TestServerSpec {

    var lobbyIndex = 0
    var botIndex = 0

    var lobbyNameGenerator = { "lobby-" + lobbyIndex++ }
    var botNameGenerator = { "bot-" + botIndex++ }

}

class TestServer(configuration: Configuration) : AutoCloseable {

    private val app = EmbeddedApp
        .fromServer(configuration.server.ratpackServer)

    fun get(path: String): ReceivedResponse =
        app
            .httpClient
            .get(path)

    fun delete(path: String): ReceivedResponse =
        app
            .httpClient
            .delete(path)

    fun post(path: String, body: Any = emptyMap<String, Any>()): ReceivedResponse =
        app
            .httpClient
            .request(path) { request ->
                request
                    .post()
                    .body { it.text(body.json()) }
                    .headers { headers ->
                        headers.set("Content-Type", "application/json")
                    }
            }

    override fun close() {
        app.close()
    }

}

suspend fun ShouldSpec.setupServer(spec: TestServerSpec.() -> Unit = {}) = autoClose(server(spec))
