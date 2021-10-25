package me.kcybulski.bricks.tournament.assertions

import io.kotest.matchers.shouldBe
import me.kcybulski.bricks.game.WonGame
import me.kcybulski.bricks.tournament.DuelResult

class DuelAssertions private constructor(private val duel: DuelResult) {

    fun playerWonTimes(name: String, times: Int): DuelAssertions {
        duel.games.count { it is WonGame && it.winner.name == name } shouldBe times
        return this
    }

    companion object {
        fun assertThat(duel: DuelResult) = DuelAssertions(duel)
    }
}
