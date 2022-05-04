package me.kcybulski.bricks.events

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import me.kcybulski.nexum.eventstore.EventStore
import me.kcybulski.nexum.eventstore.events.NoStream
import me.kcybulski.nexum.eventstore.events.Stream
import me.kcybulski.nexum.eventstore.events.StreamId
import me.kcybulski.nexum.eventstore.inmemory.InMemoryEventStore
import mu.KotlinLogging
import kotlin.reflect.KClass

class EventBus(
    private val eventStore: EventStore = InMemoryEventStore.create(),
    private val coroutine: CoroutineScope
) {

    private val logger = KotlinLogging.logger {}
    private val jackson = jacksonObjectMapper()

    init {
        eventStore.subscribeAll { event ->
            logger.info { event::class.java.simpleName + ": " + jackson.writeValueAsString(event) }
        }
    }

    fun <T : Any> send(event: T, streamId: String? = null) {
        coroutine.launch {
            eventStore.publishAsync(event, streamId.stream())
        }
    }

    fun <T : Any> subscribe(event: KClass<T>, handler: suspend (T) -> Unit) {
        eventStore.subscribe(event, handler)
    }

}

private fun String?.stream(): Stream = when(this) {
    is String -> StreamId(this)
    else -> NoStream
}
