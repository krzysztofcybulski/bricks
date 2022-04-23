package me.kcybulski.bricks.game

import me.kcybulski.bricks.api.Block
import me.kcybulski.bricks.api.Brick
import me.kcybulski.bricks.api.Identity
import me.kcybulski.bricks.api.PlayersPair
import java.util.UUID

interface GameEvent {
    val gameId: UUID
}

data class GameStartedEvent(
    override val gameId: UUID,
    val size: Int,
    val players: PlayersPair,
    val initialBlocks: Set<Block>
) : GameEvent

data class PlayerMovedEvent(
    override val gameId: UUID,
    val player: Identity,
    val brick: Brick
) : GameEvent

data class PlayerInitializedEvent(
    override val gameId: UUID,
    val player: Identity
) : GameEvent

data class PlayerNotInitializedInTimeEvent(
    override val gameId: UUID,
    val player: Identity
) : GameEvent

data class GameEndedEvent(
    override val gameId: UUID,
    val result: GameResultEvent
) : GameEvent

sealed class GameResultEvent

object TieResult : GameResultEvent()

data class GameWonResult(val player: Identity) : GameResultEvent()
