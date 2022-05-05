package me.kcybulski.bricks.gamehistory

import me.kcybulski.bricks.game.GameEvent
import me.kcybulski.nexum.eventstore.EventStore
import me.kcybulski.nexum.eventstore.events.StreamId
import java.util.UUID
import java.util.stream.Collectors

class GameHistory internal constructor(
    private val gameId: UUID,
    private val eventStore: EventStore
) {

    fun getAllEvents(): List<GameEvent> = eventStore.read {
        stream(StreamId(gameId.toString()))
    }
        .collect(Collectors.toList())
        .map { it.payload as GameEvent }

}