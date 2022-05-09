package me.kcybulski.bricks.auth

internal interface KeysRepository {

    fun saveKey(user: ApiUser, key: String)
    fun deleteKey(key: String)
    fun getUser(key: String): ApiUser?

}

class PersistedKey(
    val hash: String,
    val salt: String
)
