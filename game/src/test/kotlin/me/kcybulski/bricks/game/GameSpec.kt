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

    val settings = GameSettings(initTime = 10, moveTime = 10, randomBrickChance = 1.0)

    val coordinator = GameCoordinator(
        archer vs ciril,
        settings,
        GamesFactory(settings),
        EventBus()
    )

    suspend fun play(starting: Identity = archer.identity) = coordinator.play(starting, 3)

    should("lose game when placed out of map") {
        //given
        archer.nextMoveEither { horizontal(2, 1) }

        //when
        val endedGame = play()

        //then
        assertThat(endedGame).wonBy("Ciril")
    }

    should("play game until placed invalid brick") {
        //given
        archer.nextMoveEither { horizontal(0, 0) }
        ciril.nextMoveEither { horizontal(0, 1) }
        archer.nextMoveEither { horizontal(0, 2) }
        ciril.nextMoveEither { vertical(2, 0) }
        archer.nextMoveEither { vertical(2, 2) }

        //when
        val endedGame = play()

        //then
        assertThat(endedGame).wonBy("Ciril")
    }

    should("lose game when placed on taken field") {
        //given
        archer.nextMoveEither { horizontal(0, 0) }
        ciril.nextMoveEither { horizontal(1, 0) }

        //when
        val endedGame = play()

        //then
        assertThat(endedGame).wonBy("Archer")
    }

    should("play game until waited too long for move") {
        //given
        archer.nextMoveEither { horizontal(0, 0) }
        ciril.nextMoveEither {
            coroutineScope {
                delay(3000)
                horizontal(0, 1)
            }
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
