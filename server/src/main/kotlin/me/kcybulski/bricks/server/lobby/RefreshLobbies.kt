package me.kcybulski.bricks.server.lobby

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.UNLIMITED
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.reactive.asPublisher
import me.kcybulski.bricks.api.Identity
import me.kcybulski.bricks.game.GameEndedEvent
import me.kcybulski.nexum.eventstore.EventStore
import org.reactivestreams.Publisher
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime

class RefreshLobbies(
    eventStore: EventStore,
    private val objectMapper: ObjectMapper = jacksonObjectMapper()
) {

    private val channel = Channel<String>(capacity = UNLIMITED)

    init {
        eventStore.subscribe(GameEndedEvent::class) { channel.send("") }
    }

    suspend fun reportPing(pings: Map<Identity, Long>) {
        pings
            .mapKeys { (key, _) -> key.name }
            .let { ReportPing(it) }
            .let { objectMapper.writeValueAsString(it) }
            .let { channel.trySend(it) }
    }

    @OptIn(ExperimentalTime::class, FlowPreview::class)
    fun publisher(): Publisher<String> = channel
        .consumeAsFlow()
        .debounce(seconds(2))
        .asPublisher()

}

data class ReportPing(
    val players: Map<String, Long>
)
