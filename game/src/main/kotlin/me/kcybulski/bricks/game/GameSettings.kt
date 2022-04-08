package me.kcybulski.bricks.game

data class GameSettings(
    val initTime: Long = 1000,
    val moveTime: Long = 1000,
    val randomBrickChance: Double = 0.05
) {

    fun randomBricksAmount(mapSize: Int) = (mapSize * mapSize * randomBrickChance).toInt()

}
