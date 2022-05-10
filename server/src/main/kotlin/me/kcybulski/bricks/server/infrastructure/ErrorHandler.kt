package me.kcybulski.bricks.server.infrastructure

import mu.KotlinLogging
import ratpack.error.ClientErrorHandler
import ratpack.error.ServerErrorHandler
import ratpack.handling.Context

class ErrorHandler: ClientErrorHandler, ServerErrorHandler {

    private val logger = KotlinLogging.logger {}

    override fun error(context: Context, statusCode: Int) {
        logger.warn { "Client error resulted in: $statusCode" }
    }

    override fun error(context: Context, throwable: Throwable?) {
        logger.warn(throwable) { "Server error" }
    }
}