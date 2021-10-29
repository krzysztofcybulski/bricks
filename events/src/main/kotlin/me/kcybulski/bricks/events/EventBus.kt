package me.kcybulski.bricks.events

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import mu.KotlinLogging

class EventBus {

    private val logger = KotlinLogging.logger {}
    private val jackson = jacksonObjectMapper()

    fun <T : Any> send(event: T) {
        logger.info { event::class.java.simpleName + ": " + jackson.writeValueAsString(event) }
    }

}
