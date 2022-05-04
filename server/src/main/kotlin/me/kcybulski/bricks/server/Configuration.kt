package me.kcybulski.bricks.server

import com.github.javafaker.Faker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.newSingleThreadContext
import me.kcybulski.bricks.auth.ApiKeys
import me.kcybulski.bricks.bots.Bots
import me.kcybulski.bricks.events.CommandBus
import me.kcybulski.bricks.events.EventBus
import me.kcybulski.bricks.events.EventsModule
import me.kcybulski.bricks.gamehistory.GameHistoriesFacade
import me.kcybulski.bricks.lobbies.LobbiesModule
import me.kcybulski.bricks.lobbies.SimpleLobbiesView
import me.kcybulski.bricks.server.api.BotsApi
import me.kcybulski.bricks.server.api.CorsConfiguration
import me.kcybulski.bricks.server.api.Server
import me.kcybulski.bricks.server.api.WebsocketsRegistry
import me.kcybulski.bricks.server.api.apikeys.ApiKeysApi
import me.kcybulski.bricks.server.api.auth.AuthInterceptor
import me.kcybulski.bricks.server.api.games.GamesApi
import me.kcybulski.bricks.server.api.lobbies.LobbiesListApi
import me.kcybulski.bricks.server.api.lobbies.LobbyApi
import me.kcybulski.bricks.server.lobby.Healthchecker
import me.kcybulski.bricks.server.lobby.RefreshLobbies
import me.kcybulski.bricks.tournament.TournamentsModule
import me.kcybulski.nexum.eventstore.EventStore
import me.kcybulski.nexum.eventstore.inmemory.InMemoryEventStore
import java.lang.System.getenv
import java.util.concurrent.Executors
import java.util.concurrent.Executors.newSingleThreadExecutor

data class Configuration internal constructor(
    val commandBus: CommandBus,
    val server: Server
) {

    companion object {

        private val faker = Faker()

        fun app(
            coroutine: CoroutineScope,
            eventStore: EventStore = InMemoryEventStore.create(),
            lobbyNameGenerator: () -> String = { faker.food().dish() },
            botNameGenerator: () -> String = { faker.ancient().hero() },
            serverPort: Int? = getenv("PORT")?.toInt()
        ): Configuration {

            val websocketsRegistry = WebsocketsRegistry()

            val eventsModule = EventsModule(
                eventBus = EventBus(),
                commandBus = CommandBus(),
                initializers = listOf(
                    { eventBus, commandBus, _ -> TournamentsModule.configure(eventBus, commandBus) },
                    { eventBus, commandBus, _ -> LobbiesModule.configureInMemory(eventBus, commandBus, botNameGenerator) },
                    { eventBus, _, _ -> RefreshLobbies.configure(eventBus) },
                    { _, commandBus, modules -> Healthchecker.configure(websocketsRegistry, modules[RefreshLobbies::class], commandBus) }
                )
            )

            val lobbiesView = SimpleLobbiesView.inMemory(eventsModule.eventBus)

            val gameHistories = GameHistoriesFacade(eventStore)
            val bots = Bots.allBots(botNameGenerator)

            val apiKeys = ApiKeys.inMemoryNoHashing()

            eventsModule[Healthchecker::class].start()

            val server = Server(
                lobbiesApi = LobbiesListApi(
                    gameHistories = gameHistories,
                    refreshLobbies = eventsModule[RefreshLobbies::class],
                    lobbiesView = lobbiesView,
                    commandBus = eventsModule.commandBus,
                    singleLobbyApi = LobbyApi(
                        gameHistories = gameHistories,
                        lobbiesView = lobbiesView,
                        bots = bots,
                        apiKeys = apiKeys,
                        websocketsRegistry = websocketsRegistry,
                        commandBus = eventsModule.commandBus,
                        coroutine = coroutine
                    )
                ),
                gamesApi = GamesApi(gameHistories),
                botsApi = BotsApi(bots),
                apiKeysApi = ApiKeysApi(apiKeys),
                corsConfiguration = CorsConfiguration(),
                authInterceptor = AuthInterceptor.fromEnvironmentVariables(),
                port = serverPort
            )

            return Configuration(
                commandBus = eventsModule.commandBus,
                server = server
            )
        }

    }

}
