package me.kcybulski.bricks.auth

import java.util.UUID.randomUUID

internal object UUIDKeyGenerator {

    fun generateKey(): ApiKey =
        randomUUID()
            .toString()
            .replace("-", "")
            .let(::ApiKey)

}