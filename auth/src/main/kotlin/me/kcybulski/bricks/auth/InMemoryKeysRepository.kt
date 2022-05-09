package me.kcybulski.bricks.auth

internal class InMemoryKeysRepository : KeysRepository {

    private val memory: MutableMap<String, ApiUser> = mutableMapOf()

    override fun saveKey(user: ApiUser, key: String) {
        memory[key] = user
    }

    override fun deleteKey(key: String) {
        memory.remove(key)
    }

    override fun getUser(key: String): ApiUser? {
        return memory[key]
    }

}
