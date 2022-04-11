package me.kcybulski.bricks.server.api

import ratpack.handling.Context
import java.lang.System.getenv

class CorsConfiguration {

    private val api = getenv()["FRONT_URL"] ?: "http://localhost:3000"

    fun addCORSHeaders(ctx: Context) = ctx
        .header("Access-Control-Allow-Origin", api)
        .header("Access-Control-Allow-Headers", "Accept, Content-Type")
        .header("Access-Control-Allow-Methods", "GET, POST")
        .next()

}
