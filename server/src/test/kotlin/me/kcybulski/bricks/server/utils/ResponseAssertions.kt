package me.kcybulski.bricks.server.utils

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.kotest.matchers.shouldBe
import ratpack.http.client.ReceivedResponse

object ResponseAssertions {

    private val objectMapper = jacksonObjectMapper()

    val emptyList = listOf<Any>()
    val emptyObject = mapOf<String, Any>()

    infix fun ReceivedResponse.jsonShouldBe(list: List<Any>) = asList() shouldBe list

    infix fun ReceivedResponse.jsonShouldBe(map: Map<String, Any>) = asMap() shouldBe map

    private fun ReceivedResponse.asMap(): Map<String, Any> =
        objectMapper.readValue(body.text, Map::class.java) as Map<String, Any>

    private fun ReceivedResponse.asList(): List<Map<String, Any>> =
        objectMapper.readValue(body.text, List::class.java) as List<Map<String, Any>>
}
