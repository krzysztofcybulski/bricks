package me.kcybulski.bricks.tournament

import me.kcybulski.bricks.game.GameSettings

data class TournamentSettings(
    val game: GameSettings,
    val mapSizesPerDuel: List<Int>
) {

    companion object {
        fun settings(configuration: TournamentSettingsBuilder.() -> Unit = {}) =
            TournamentSettingsBuilder()
                .apply(configuration)
                .build()
    }
}

class TournamentSettingsBuilder {

    var initTime: Long = 1000
    var moveTime: Long = 1000
    var mapSizesPerDuel: List<Int> = listOf(5, 10, 100)

    fun build() = TournamentSettings(
        GameSettings(initTime, moveTime),
        mapSizesPerDuel
    )
}
