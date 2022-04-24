package me.kcybulski.bricks.server

import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.coroutineScope
import kotlin.coroutines.CoroutineContext

suspend fun <E> ReceiveChannel<E>.throttle(
    wait: Long = 200,
    context: CoroutineContext
): ReceiveChannel<E> = coroutineScope {
    produce(context) {
        var nextTime = 0L
        consumeEach {
            val curTime = System.currentTimeMillis()
            if (curTime >= nextTime) {
                nextTime = curTime + wait
                send(it)
            }
        }
    }
}
