package me.kcybulski.bricks.auth

import me.kcybulski.bricks.events.EventBus


class ApiKeys internal constructor(
    private val generator: UUIDKeyGenerator,
    private val repository: KeysRepository,
    private val eventBus: EventBus
) {

    fun generateKey(user: ApiUser): ApiKey {
        val key = generator.generateKey()
        repository.saveKey(user, key.raw)
        eventBus.send(UserRegistered(user), user.id)
        return key
    }

    fun revoke(user: ApiUser) {
        repository.deleteKey(user.name)
    }

    fun authorize(key: ApiKey): ApiUser? =
        repository.getUser(key.raw)

    companion object {

        fun inMemoryNoHashing(eventBus: EventBus) = ApiKeys(
            generator = UUIDKeyGenerator,
            repository = InMemoryKeysRepository(),
            eventBus = eventBus
        )

    }

}

data class UserRegistered(
    val apiUser: ApiUser
)

data class ApiUser(
    val id: String,
    val name: String,
    val avatar: String
)

data class ApiKey(val raw: String)
