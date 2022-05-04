package me.kcybulski.bricks.auth

internal interface KeysRepository {

    fun saveKey(user: String, key: String)
    fun deleteKey(key: String)
    fun getUser(key: String): String?

}

class PersistedKey(
    val hash: String,
    val salt: String
)