package me.kcybulski.bricks.server

import kotlinx.coroutines.job
import kotlinx.coroutines.runBlocking
import me.kcybulski.bricks.bots.Bots
import me.kcybulski.bricks.events.EventBus
import me.kcybulski.bricks.gamehistory.GameHistoriesFacade
import me.kcybulski.bricks.server.api.CorsConfiguration
import me.kcybulski.bricks.server.api.Server
import me.kcybulski.bricks.server.lobby.Entrance
import me.kcybulski.bricks.server.lobby.Healthchecker
import me.kcybulski.bricks.server.lobby.LobbyFactory
import me.kcybulski.bricks.server.lobby.OpenLobby
import me.kcybulski.bricks.tournament.TournamentFacade
import me.kcybulski.nexum.eventstore.inmemory.InMemoryEventStore

fun main() = runBlocking {
    val eventStore = InMemoryEventStore.create()

    val entrance = Entrance(LobbyFactory(), this)
    val tournaments = TournamentFacade(EventBus(eventStore))
    val gameHistory = GameHistoriesFacade(eventStore)
    val bots = Bots()

    Healthchecker(entrance).start()

    entrance.newLobby()

    val server = Server(
        entrance = entrance,
        tournaments = tournaments,
        gameHistories = gameHistory,
        bots = bots,
        corsConfiguration = CorsConfiguration(),
        coroutineScope = this
    )

    server.start()

    coroutineContext.job.join()
}
