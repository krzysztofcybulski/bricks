package me.kcybulski.bricks.events

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.launch
import me.kcybulski.nexum.eventstore.subscribing.AllTypesHandler
import me.kcybulski.nexum.eventstore.subscribing.EventHandler
import me.kcybulski.nexum.eventstore.subscribing.EventTypeHandler
import mu.KotlinLogging
import java.util.concurrent.Executors
import kotlin.reflect.KClass

class CommandBus(
    private val coroutine: CoroutineScope = CoroutineScope(Executors.newSingleThreadExecutor().asCoroutineDispatcher()),
    private val handlers: MutableList<EventHandler<*>> = mutableListOf()
) {

    private val logger = KotlinLogging.logger {}
    private val jackson = jacksonObjectMapper()

    init {
        handlers += AllTypesHandler<Any> { event ->
            logger.info { event::class.java.simpleName + ": " + jackson.writeValueAsString(event) }
        }
    }

    fun <T : Any> send(command: T) {
        coroutine.launch {
            handlers
                .filter { it.accepting(command::class) }
                .map { it.handler as (suspend (Any) -> Unit) }
                .map { it(command) }
        }
    }

    fun <T : Any> on(command: KClass<T>, handler: suspend (T) -> Unit) {
        handlers += EventTypeHandler(command, handler)
    }

}