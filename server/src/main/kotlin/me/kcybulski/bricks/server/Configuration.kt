package me.kcybulski.bricks.server

import com.github.javafaker.Faker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.newSingleThreadContext
import me.kcybulski.bricks.bots.Bots
import me.kcybulski.bricks.events.EventBus
import me.kcybulski.bricks.gamehistory.GameHistoriesFacade
import me.kcybulski.bricks.server.api.CorsConfiguration
import me.kcybulski.bricks.server.api.Server
import me.kcybulski.bricks.server.lobby.Entrance
import me.kcybulski.bricks.server.lobby.LobbyFactory
import me.kcybulski.bricks.server.lobby.RefreshLobbies
import me.kcybulski.bricks.tournament.TournamentFacade
import me.kcybulski.nexum.eventstore.EventStore
import me.kcybulski.nexum.eventstore.inmemory.InMemoryEventStore
import java.lang.System.getenv

data class Configuration internal constructor(
    val eventStore: EventStore,
    val eventBus: EventBus,
    val entrance: Entrance,
    val refreshLobbies: RefreshLobbies,
    val server: Server
) {

    companion object {

        private val faker = Faker()

        fun app(
            coroutine: CoroutineScope,
            eventStore: EventStore = InMemoryEventStore.create(),
            lobbyNameGenerator: () -> String = { faker.food().dish() },
            botNameGenerator: () -> String = { faker.ancient().hero() }
        ): Configuration {
            val eventBus = EventBus(eventStore)

            val lobbyFactory = LobbyFactory(eventBus, lobbyNameGenerator)
            val entrance = Entrance(lobbyFactory, eventBus)
            val refreshLobbies = RefreshLobbies(eventStore, CoroutineScope(newSingleThreadContext("refresh")))

            val server = Server(
                entrance = entrance,
                tournaments = TournamentFacade(eventBus),
                gameHistories = GameHistoriesFacade(eventStore),
                bots = Bots.allBots(botNameGenerator),
                corsConfiguration = CorsConfiguration(),
                coroutineScope = coroutine,
                refreshLobbies = refreshLobbies,
                port = getenv("PORT")?.toInt() ?: 5050
            )

            return Configuration(
                eventStore = eventStore,
                eventBus = eventBus,
                entrance = entrance,
                refreshLobbies = refreshLobbies,
                server = server
            )
        }

    }

}
