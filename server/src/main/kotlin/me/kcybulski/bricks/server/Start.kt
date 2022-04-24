package me.kcybulski.bricks.server

import kotlinx.coroutines.job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import me.kcybulski.bricks.bots.Bots
import me.kcybulski.bricks.events.EventBus
import me.kcybulski.bricks.gamehistory.GameHistoriesFacade
import me.kcybulski.bricks.server.api.CorsConfiguration
import me.kcybulski.bricks.server.api.Server
import me.kcybulski.bricks.server.lobby.Entrance
import me.kcybulski.bricks.server.lobby.Healthchecker
import me.kcybulski.bricks.server.lobby.RefreshLobbies
import me.kcybulski.bricks.tournament.TournamentFacade
import me.kcybulski.nexum.eventstore.inmemory.InMemoryEventStore
import java.lang.System.getenv

fun main() = runBlocking {
    val eventStore = InMemoryEventStore.create()
    val eventBus = EventBus(eventStore)

    val entrance = Entrance.createWithLobbies(1)
    val refreshLobbies = RefreshLobbies(eventStore)

    launch {
        Healthchecker.startForEntrance(entrance, refreshLobbies)
    }

    val server = Server(
        entrance = entrance,
        tournaments = TournamentFacade(eventBus),
        gameHistories = GameHistoriesFacade(eventStore),
        bots = Bots.allBots(),
        corsConfiguration = CorsConfiguration(),
        coroutineScope = this,
        refreshLobbies = refreshLobbies,
        port = getenv("PORT").toInt()
    )

    server.start()
    coroutineContext.job.join()
}
