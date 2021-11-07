package me.kcybulski.bricks.server

import me.kcybulski.bricks.events.EventBus
import me.kcybulski.bricks.gamehistory.GameHistoriesFacade
import me.kcybulski.bricks.server.api.Server
import me.kcybulski.bricks.server.lobby.Entrance
import me.kcybulski.bricks.server.lobby.Healthchecker
import me.kcybulski.bricks.server.lobby.LobbyFactory
import me.kcybulski.bricks.tournament.TournamentFacade
import me.kcybulski.nexum.eventstore.inmemory.InMemoryEventStore

fun main() {
    val eventStore = InMemoryEventStore.create()

    val entrance = Entrance(LobbyFactory())
    val tournaments = TournamentFacade(EventBus(eventStore))
    val gameHistory = GameHistoriesFacade(eventStore)

    Healthchecker(entrance).start()

    entrance.newLobby()

    Server(entrance, tournaments, gameHistory).run { start() }
}
