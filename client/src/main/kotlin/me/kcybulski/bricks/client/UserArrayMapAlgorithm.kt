package me.kcybulski.bricks.client

import me.kcybulski.bricks.api.Block
import me.kcybulski.bricks.api.Brick
import me.kcybulski.bricks.api.GameInitialized
import me.kcybulski.bricks.api.MoveTrigger
import me.kcybulski.bricks.api.MoveTrigger.FirstMove
import me.kcybulski.bricks.api.MoveTrigger.OpponentMoved

abstract class UserArrayMapAlgorithm(
    suffix: String = ""
) : UserAlgorithm(suffix) {

    private var map: Array<Array<MapField>> = Array(0) { Array(0) { Free } }

    final override suspend fun initialize(gameInitialized: GameInitialized) {
        map = Array(gameInitialized.size) { x ->
            Array(gameInitialized.size) { y ->
                if (Block(x, y) in gameInitialized.initialBlocks) Taken else Free
            }
        }
    }

    final override suspend fun move(last: MoveTrigger): Brick =
        when (last) {
            FirstMove -> move(map)
            is OpponentMoved -> {
                save(last.brick)
                move(map)
            }
        }
            .also(this::save)

    abstract suspend fun move(map: Array<Array<MapField>>): Brick

    private fun save(brick: Brick) {
        brick.blocks.forEach { (x, y) -> map[x][y] = Taken }
    }

    sealed class MapField

    object Free : MapField()
    object Taken : MapField()

}
