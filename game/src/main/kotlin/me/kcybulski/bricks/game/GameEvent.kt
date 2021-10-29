package me.kcybulski.bricks.game

import java.util.UUID

interface GameEvent {
    val gameId: UUID
}

data class GameStartedEvent(override val gameId: UUID, val players: PlayersPair): GameEvent
data class PlayerMovedEvent(override val gameId: UUID, val player: Identity, val brick: Brick): GameEvent
data class PlayerInitializedEvent(override val gameId: UUID, val player: Identity): GameEvent
data class PlayerNotInitializedInTimeEvent(override val gameId: UUID, val player: Identity): GameEvent
