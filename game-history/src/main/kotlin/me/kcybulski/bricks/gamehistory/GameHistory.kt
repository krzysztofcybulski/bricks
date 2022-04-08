package me.kcybulski.bricks.gamehistory

import me.kcybulski.bricks.game.Block
import me.kcybulski.bricks.game.GameStartedEvent
import me.kcybulski.bricks.game.Identity
import me.kcybulski.bricks.game.PlayerMovedEvent
import me.kcybulski.bricks.game.PlayersPair
import me.kcybulski.bricks.gamehistory.MapBlock.Empty
import me.kcybulski.bricks.gamehistory.MapBlock.Taken
import me.kcybulski.nexum.eventstore.EventStore
import me.kcybulski.nexum.eventstore.events.StreamId
import java.util.UUID

class GameHistory internal constructor(
    private val gameId: UUID,
    private val eventStore: EventStore
) {

    fun at(time: Int): GameMap? = eventStore.project(null as GameMap?, {
        limit = time + 3
        stream(StreamId(gameId.toString()))
    }) { map, event ->
        when (event) {
            is GameStartedEvent -> GameMap(event.players, Array(event.size) { Array(event.size) { Empty } })
                .withStartingBlocks(event.startingBlocks)
            is PlayerMovedEvent -> map?.placed(event)
            else -> map
        }
    }
}

class GameMap(
    val players: PlayersPair,
    val blocks: Array<Array<MapBlock>>
) {

    fun placed(event: PlayerMovedEvent) = GameMap(
        players,
        event.brick.blocks.fold(blocks) { map, pos -> map.with(pos, Taken(event.player)) }
    )

    fun withStartingBlocks(startingBlocks: Set<Block>) = GameMap(
        players,
        startingBlocks.fold(blocks) { blocks, b -> blocks.with(b, MapBlock.StartingBlock) }
    )

}

sealed class MapBlock {

    object Empty : MapBlock()
    object StartingBlock : MapBlock()
    class Taken(val owner: Identity) : MapBlock()

}

private inline fun <reified T> Array<Array<T>>.with(block: Block, new: T): Array<Array<T>> =
    with(block.y) { it.with(block.x) { new } }

private inline fun <reified T> Array<T>.with(position: Int, new: (T) -> T): Array<T> =
    mapIndexed { index, old -> if (index == position) new(old) else old }
        .toTypedArray()
