package me.kcybulski.bricks.server.api.apikeys

import io.netty.buffer.PooledByteBufAllocator.DEFAULT
import me.kcybulski.bricks.server.api.auth.Verified
import ratpack.http.client.HttpClient
import ratpack.server.ServerConfig.DEFAULT_MAX_CONTENT_LENGTH
import java.time.Duration
import java.time.temporal.ChronoUnit

class UserIdentifyProvider(
    private val httpClient: HttpClient = defaultHttpClient()
) {

//    fun getIdentity(verified: Verified) =
//        httpClient.get()

}

data class UserIdentity(
    val id: String,
    val username: String,
    val name: String,
    val avatar: String
)

private fun defaultHttpClient() =
    HttpClient.of { httpClientSpec ->
        httpClientSpec
            .poolSize(1)
            .useJdkAddressResolver()
            .connectTimeout(Duration.of(5, ChronoUnit.SECONDS))
            .maxContentLength(DEFAULT_MAX_CONTENT_LENGTH)
            .responseMaxChunkSize(16384)
            .readTimeout(Duration.of(5, ChronoUnit.SECONDS))
            .byteBufAllocator(DEFAULT)
    }
