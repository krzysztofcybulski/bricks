package me.kcybulski.bricks.server

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import me.kcybulski.bricks.server.utils.ResponseAssertions.asList
import me.kcybulski.bricks.server.utils.setupServer

class BotsSpec : ShouldSpec({

    should("list all available bots") {
        //given
        val server = setupServer {  }

        //expect
        val response = server.get("bots")
        response.asList() shouldBe listOf(
            mapOf(
                "name" to "Alpha",
            )
        )
    }

})
