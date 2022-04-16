package me.kcybulski.bricks.game

import io.kotest.core.spec.IsolationMode.InstancePerTest
import io.kotest.core.spec.style.ShouldSpec
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import me.kcybulski.bricks.events.EventBus
import me.kcybulski.bricks.test.TestAlgorithm
import me.kcybulski.bricks.test.assertions.GameAssertions.Companion.assertThat
import me.kcybulski.bricks.test.horizontal
import me.kcybulski.bricks.test.vertical

class GameSpec : ShouldSpec({

    isolationMode = InstancePerTest

    val archer = TestAlgorithm("Archer")
    val ciril = TestAlgorithm("Ciril")

    val settings = GameSettings(
        initTime = 10,
        moveTime = 10,
        randomBrickChance = 0.0
    )

    val coordinator = GameCoordinator(
        archer vs ciril,
        settings,
        GamesFactory(settings),
        EventBus()
    )

    suspend fun play(starting: Identity = archer.identity) = coordinator.play(starting, 3)

    should("lose game when placed out of map") {
        //given
        archer.nextMove { horizontal(2, 1) }

        //when
        val endedGame = play()

        //then
        assertThat(endedGame).wonBy("Ciril")
    }

    should("play game until placed invalid brick") {
        //given
        archer.nextMove { horizontal(0, 0) }
        ciril.nextMove { horizontal(0, 1) }
        archer.nextMove { horizontal(0, 2) }
        ciril.nextMove { vertical(2, 0) }
        archer.nextMove { vertical(2, 2) }

        //when
        val endedGame = play()

        //then
        assertThat(endedGame).wonBy("Ciril")
    }

    should("lose game when placed on taken field") {
        //given
        archer.nextMove { horizontal(0, 0) }
        ciril.nextMove { horizontal(1, 0) }

        //when
        val endedGame = play()

        //then
        assertThat(endedGame).wonBy("Archer")
    }

    should("play game until waited too long for move") {
        //given
        archer.nextMove { horizontal(0, 0) }
        ciril.nextMove {
            delay(3000)
            horizontal(0, 1)
        }

        //when
        val endedGame = play()

        //then
        assertThat(endedGame).wonBy("Archer")
    }

    should("end game when player not initialized in given time") {
        //given
        archer.initTime = 1000L

        //when
        val endedGame = play()

        //then
        assertThat(endedGame).wonBy("Ciril")
    }

    should("tie game when all players not initialized in given time") {
        //given
        archer.initTime = 1000L
        ciril.initTime = 1000L

        //when
        val endedGame = play()

        //then
        assertThat(endedGame).tied()
    }
})
