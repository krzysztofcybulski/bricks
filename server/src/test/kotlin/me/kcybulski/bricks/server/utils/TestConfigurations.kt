package me.kcybulski.bricks.server.utils

import kotlinx.coroutines.CoroutineScope
import me.kcybulski.bricks.server.Configuration
import ratpack.test.embed.EmbeddedApp

object TestConfigurations {

    suspend fun testServer(coroutineScope: CoroutineScope, spec: suspend (EmbeddedApp) -> Unit) {
        var lobbyIndex = 0
        var botIndex = 0
        val server = Configuration.app(
            coroutine = coroutineScope,
            lobbyNameGenerator = { "lobby-" + lobbyIndex++ },
            botNameGenerator = { "bot-" + botIndex++ }
        ).server
        val app = EmbeddedApp.fromServer(server.ratpackServer)
        spec(app)
        app.close()
    }

}
