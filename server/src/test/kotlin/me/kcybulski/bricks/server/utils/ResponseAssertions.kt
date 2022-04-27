package me.kcybulski.bricks.server.utils

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import ratpack.http.client.ReceivedResponse

object ResponseAssertions {

    private val objectMapper = jacksonObjectMapper()

    val emptyList = listOf<Any>()
    val emptyObject = mapOf<String, Any>()

    fun ReceivedResponse.asMap(): Map<String, Any> =
        objectMapper.readValue(body.text, Map::class.java) as Map<String, Any>

    fun ReceivedResponse.asList(): List<Map<String, Any>> =
        objectMapper.readValue(body.text, List::class.java) as List<Map<String, Any>>

    fun Any.json() = objectMapper.writeValueAsString(this)

}
