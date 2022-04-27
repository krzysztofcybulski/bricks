package me.kcybulski.bricks.server.utils

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import kotlinx.coroutines.CoroutineScope
import me.kcybulski.bricks.server.Configuration
import ratpack.http.client.ReceivedResponse
import ratpack.test.embed.EmbeddedApp

object TestConfigurations {

    private val objectMapper = jacksonObjectMapper()

    suspend fun testServer(coroutineScope: CoroutineScope, spec: suspend (EmbeddedApp) -> Unit) {
        var lobbyIndex = 0
        val server = Configuration.app(
            coroutine = coroutineScope,
            lobbyNameGenerator = { "" + lobbyIndex++ }
        ).server
        val app = EmbeddedApp.fromServer(server.ratpackServer)
        spec(app)
        app.close()
    }

    fun map(response: ReceivedResponse): Map<String, Any> =
        objectMapper.readValue(response.body.text, Map::class.java) as Map<String, Any>

    fun list(response: ReceivedResponse): List<Map<String, Any>> =
        objectMapper.readValue(response.body.text, List::class.java) as List<Map<String, Any>>

}
