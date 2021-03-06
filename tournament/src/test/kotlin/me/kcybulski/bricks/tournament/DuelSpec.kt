package me.kcybulski.bricks.tournament

import io.kotest.core.spec.IsolationMode.InstancePerTest
import io.kotest.core.spec.style.ShouldSpec
import me.kcybulski.bricks.events.EventBus
import me.kcybulski.bricks.game.GameCoordinator
import me.kcybulski.bricks.game.GameSettings
import me.kcybulski.bricks.game.GamesFactory
import me.kcybulski.bricks.game.vs
import me.kcybulski.bricks.test.TestAlgorithm
import me.kcybulski.bricks.test.horizontal
import me.kcybulski.bricks.tournament.assertions.DuelAssertions.Companion.assertThat
import java.util.UUID.randomUUID

class DuelSpec : ShouldSpec({

    isolationMode = InstancePerTest

    val archer = TestAlgorithm("Archer")
    val ciril = TestAlgorithm("Ciril")

    val settings = GameSettings(
        initTime = 10,
        moveTime = 10,
        randomBrickChance = 0.0
    )

    val events = EventBus()

    val coordinator = GameCoordinator(
        archer vs ciril,
        settings,
        GamesFactory(settings),
        events
    )

    val duel = DuelCoordinator(
        randomUUID(),
        coordinator,
        events
    )

    should("players take turns when playing duel") {
        //given
        archer.defaultMove { horizontal(0, 0) }
        ciril.defaultMove { horizontal(0, 0) }

        //when
        val duelResult = duel.duel(3, 5, 10)

        //then
        assertThat(duelResult)
            .playerWonTimes("Archer", 3)
            .playerWonTimes("Ciril", 3)
    }

})
