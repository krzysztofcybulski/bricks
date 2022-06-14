package me.kcybulski.bricks.server.views.gamehistory

import me.kcybulski.bricks.api.Identity
import me.kcybulski.bricks.events.EventBus
import me.kcybulski.bricks.game.GameEndedEvent
import me.kcybulski.bricks.game.GameStartedEvent
import me.kcybulski.bricks.game.GameWonResult
import me.kcybulski.bricks.game.PlayerMovedEvent
import me.kcybulski.bricks.game.TieResult
import me.kcybulski.bricks.server.views.Avatars
import me.kcybulski.bricks.server.views.users.UserViewsReadModel
import java.time.Clock
import java.util.UUID

class GamesHistoryReadModel private constructor(
    private val repository: GameHistoryRepository,
    private val users: UserViewsReadModel,
    private val clock: Clock = Clock.systemUTC()
) {

    fun find(gameId: UUID): GameHistoryView? = repository.find(gameId)

    private fun onGameStarted(event: GameStartedEvent) {
        repository.update(
            GameHistoryView(
                id = event.gameId,
                startedAt = clock.instant().toString(),
                players = toPlayerView(event.players.first) to toPlayerView(event.players.second),
                mapSize = event.size,
                initialBlocks = event.initialBlocks,
                moves = emptyList(),
                winner = null
            )
        )
    }

    private fun onPlayerMoved(event: PlayerMovedEvent) {
        repository.find(event.gameId)
            ?.let {
                it.copy(
                    moves = it.moves + MoveInGameView(
                        event.player.name,
                        event.brick,
                        clock.instant().toString()
                    )
                )
            }
            ?.let { repository.update(it) }
    }

    private fun onGameEnded(event: GameEndedEvent) {
        repository.find(event.gameId)
            ?.copy(
                winner = when (event.result) {
                    TieResult -> "-"
                    is GameWonResult -> (event.result as GameWonResult).player.name
                }
            )
            ?.let { repository.update(it) }
    }

    private fun toPlayerView(identity: Identity) =
        users.find(identity.name)
            ?.let {
                PlayerView(
                    name = it.name,
                    image = it.avatarUrl,
                    color = it.color
                )
            }
            ?: PlayerView(
                name = identity.name,
                image = Avatars.generateForPlayer(identity),
                color = "#${Avatars.color(identity.name)}"
            )

    companion object {

        fun configureInMemory(
            eventBus: EventBus,
            users: UserViewsReadModel
        ): GamesHistoryReadModel {
            val module = GamesHistoryReadModel(InMemoryGameHistoryRepository(), users)

            eventBus.subscribe(GameStartedEvent::class, module::onGameStarted)
            eventBus.subscribe(PlayerMovedEvent::class, module::onPlayerMoved)
            eventBus.subscribe(GameEndedEvent::class, module::onGameEnded)

            return module
        }

    }

}
