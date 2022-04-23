package me.kcybulski.bricks.test.assertions

import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import me.kcybulski.bricks.game.Game
import me.kcybulski.bricks.game.TiedGame
import me.kcybulski.bricks.game.WonGame

class GameAssertions private constructor(private val game: Game) {

    fun wonBy(player: String): GameAssertions {
        game.shouldBeInstanceOf<WonGame>()
        game.winner.name shouldBe player
        return this
    }

    fun tied(): GameAssertions {
        game.shouldBeInstanceOf<TiedGame>()
        return this
    }

    companion object {
        fun assertThat(game: Game) = GameAssertions(game)
    }
}
