package me.kcybulski.bricks.game

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withTimeoutOrNull
import me.kcybulski.bricks.game.MoveTrigger.FirstMove
import me.kcybulski.bricks.game.MoveTrigger.OpponentMoved

class GameCoordinator(
    private val algorithms: AlgorithmsPair,
    private val gameSettings: GameSettings
) {

    val players = algorithms.players()

    suspend fun play(startingPlayer: Identity, mapSize: Int): EndedGame =
        initialize(startingPlayer, NewGame(players, mapSize))

    private suspend fun next(game: Game, lastMove: MoveTrigger): EndedGame =
        when (game) {
            is NewGame -> throw IllegalStateException("Game cannot be new")
            is InProgressGame -> currentPlayerMove(game, lastMove)
            is EndedGame -> game
        }

    private suspend fun initialize(startingPlayer: Identity, game: NewGame): EndedGame {
        val initializationResults = initializePlayers(game)
        return when {
            initializationResults.allInitializedInTime() -> next(game.started(startingPlayer), FirstMove)
            initializationResults.allExceededTime() -> game.tie()
            else -> game.won(initializationResults.playerThatInitializedInTime())
        }
    }

    private suspend fun currentPlayerMove(game: InProgressGame, lastMove: MoveTrigger) =
        withTimeoutOrNull(gameSettings.moveTime) { algorithms[game.currentPlayer].move(lastMove) }
            ?.let { next(game.placed(it), OpponentMoved(it)) }
            ?: game.lost()

    private suspend fun initializePlayers(game: NewGame): List<PlayerInitialized> = coroutineScope {
        awaitAll(
            async { init(algorithms.first, game) },
            async { init(algorithms.second, game) }
        )
    }

    private suspend fun init(player: Algorithm, game: NewGame) =
        withTimeoutOrNull(gameSettings.initTime) { player.initialize(game) }
            ?.let { PlayerInitializedInTime(player.identity) }
            ?: PlayerExceededInitTimeout(player.identity)

}

