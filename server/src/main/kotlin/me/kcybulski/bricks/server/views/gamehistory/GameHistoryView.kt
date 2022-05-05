package me.kcybulski.bricks.server.views.gamehistory

import me.kcybulski.bricks.api.Block
import me.kcybulski.bricks.api.Brick
import java.time.Instant
import java.util.UUID

data class GameHistoryView(
    val id: UUID,
    val startedAt: Instant,
    val players: Pair<PlayerView, PlayerView>,
    val mapSize: Int,
    val initialBlocks: Set<Block>,
    val moves: List<MoveInGameView>,
    val winner: String?
)

data class MoveInGameView(
    val player: String,
    val brick: Brick,
    val time: Instant
)

data class PlayerView(
    val name: String,
    val image: String
)