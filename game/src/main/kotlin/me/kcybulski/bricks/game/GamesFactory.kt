package me.kcybulski.bricks.game

import me.kcybulski.bricks.api.Block
import me.kcybulski.bricks.api.GameInitialized
import me.kcybulski.bricks.api.PlayersPair
import java.util.UUID.randomUUID
import kotlin.random.Random

class GamesFactory(
    private val gameSettings: GameSettings
) {

    fun createNewGame(
        players: PlayersPair,
        mapSize: Int
    ): Pair<GameInitialized, NewGame> {
        val randomBlocks = randomBlocks(mapSize)
        val gameId = randomUUID()
        val game = NewGame(gameId, players, GameMap.of(mapSize).withBlocks(randomBlocks))
        val gameInitialized = GameInitialized(gameId, mapSize, players, randomBlocks)
        return gameInitialized to game
    }

    private fun randomBlocks(mapSize: Int) =
        (0 until gameSettings.randomBricksAmount(mapSize))
            .map { Block(Random.nextInt(mapSize), Random.nextInt(mapSize)) }
            .toSet()
}
