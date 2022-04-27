package me.kcybulski.bricks.server.utils

import io.kotest.core.spec.style.scopes.ShouldSpecRootContext
import io.kotest.core.test.TestContext
import me.kcybulski.bricks.server.utils.TestConfigurations.testServer
import ratpack.test.embed.EmbeddedApp

fun ShouldSpecRootContext.should(name: String, test: suspend (EmbeddedApp, TestContext) -> Unit) =
    should(name) {
        testServer(this) { server -> test(server, this) }
    }
