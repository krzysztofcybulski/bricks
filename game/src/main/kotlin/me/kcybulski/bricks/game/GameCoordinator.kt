package me.kcybulski.bricks.game

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withTimeoutOrNull
import me.kcybulski.bricks.events.EventBus
import me.kcybulski.bricks.game.MoveTrigger.FirstMove
import me.kcybulski.bricks.game.MoveTrigger.OpponentMoved

class GameCoordinator(
    private val algorithms: AlgorithmsPair,
    private val gameSettings: GameSettings,
    private val gamesFactory: GamesFactory,
    private val events: EventBus
) {

    val players = algorithms.players()

    suspend fun play(startingPlayer: Identity, mapSize: Int): EndedGame =
        gamesFactory.createNewGame(players, mapSize)
            .also { (gameInitialized, game) -> events.send(gameInitialized.toStartedEvent(), game.id.toString()) }
            .let { (gameInitialized, game) -> initialize(startingPlayer, game, gameInitialized) }
            .also { game -> events.send(game.toEndedEvent(), game.id.toString()) }

    private suspend fun next(game: Game, lastMove: MoveTrigger): EndedGame =
        when (game) {
            is NewGame -> throw IllegalStateException("Game cannot be new")
            is InProgressGame -> currentPlayerMove(game, lastMove)
            is EndedGame -> game
        }

    private suspend fun initialize(
        startingPlayer: Identity,
        game: NewGame,
        gameInitialized: GameInitialized
    ): EndedGame {
        val initializationResults = initializePlayers(game, gameInitialized)
        return when {
            initializationResults.allInitializedInTime() -> next(game.started(startingPlayer), FirstMove)
            initializationResults.allExceededTime() -> game.tie()
            else -> game.won(initializationResults.playerThatInitializedInTime())
        }
    }

    private suspend fun currentPlayerMove(game: InProgressGame, lastMove: MoveTrigger) =
        withTimeoutOrNull(gameSettings.moveTime) { algorithms[game.currentPlayer].move(lastMove) }
            ?.also { events.send(PlayerMovedEvent(game.id, game.currentPlayer, it), game.id.toString()) }
            ?.let { next(game.placed(it), OpponentMoved(it)) }
            ?: game.lost()

    private suspend fun initializePlayers(game: NewGame, gameInitialized: GameInitialized): List<PlayerInitialized> =
        coroutineScope {
            awaitAll(
                async { init(algorithms.first, gameInitialized) },
                async { init(algorithms.second, gameInitialized) }
            )
                .onEach { sendEvent(it, game) }
        }

    private suspend fun init(player: Algorithm, gameInitialized: GameInitialized) =
        withTimeoutOrNull(gameSettings.initTime) { player.initialize(gameInitialized) }
            ?.let { PlayerInitializedInTime(player.identity) }
            ?: PlayerExceededInitTimeout(player.identity)

    private fun sendEvent(playerInitialized: PlayerInitialized, game: Game) = when (playerInitialized) {
        is PlayerInitializedInTime -> PlayerInitializedEvent(game.id, playerInitialized.player)
        is PlayerExceededInitTimeout -> PlayerNotInitializedInTimeEvent(game.id, playerInitialized.player)
    }
        .let { events.send(it, game.id.toString()) }

}

private fun GameInitialized.toStartedEvent() =
    GameStartedEvent(
        gameId = gameId,
        size = size,
        players = players,
        initialBlocks = initialBlocks
    )

private fun EndedGame.toEndedEvent() =
    GameEndedEvent(
        gameId = id,
        result = when(this) {
            is WonGame -> GameWonResult(winner)
            is TiedGame -> TieResult
        }
    )
