package me.kcybulski.bricks.auth


class ApiKeys internal constructor(
    private val generator: UUIDKeyGenerator,
    private val repository: KeysRepository
) {

    fun generateKey(user: ApiUser): ApiKey {
        val key = generator.generateKey()
        repository.saveKey(user, key.raw)
        return key
    }

    fun revoke(user: ApiUser) {
        repository.deleteKey(user.name)
    }

    fun authorize(key: ApiKey): ApiUser? =
        repository.getUser(key.raw)

    companion object {

        fun inMemoryNoHashing() = ApiKeys(
            generator = UUIDKeyGenerator,
            repository = InMemoryKeysRepository()
        )

    }

}

data class ApiUser(val id: String, val name: String)

data class ApiKey(val raw: String)
