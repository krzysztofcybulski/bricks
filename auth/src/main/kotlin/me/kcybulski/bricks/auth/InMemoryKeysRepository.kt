package me.kcybulski.bricks.auth

internal class InMemoryKeysRepository : KeysRepository {

    private val memory: MutableMap<String, String> = mutableMapOf()

    override fun saveKey(user: String, key: String) {
        memory[key] = user
    }

    override fun deleteKey(key: String) {
        memory.remove(key)
    }

    override fun getUser(key: String): String? {
        return memory[key]
    }

}