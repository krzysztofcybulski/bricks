package me.kcybulski.bricks.server

import com.github.javafaker.Faker
import kotlinx.coroutines.CoroutineScope
import me.kcybulski.bricks.auth.ApiKeys
import me.kcybulski.bricks.bots.Bots
import me.kcybulski.bricks.events.CommandBus
import me.kcybulski.bricks.events.EventBus
import me.kcybulski.bricks.events.EventsModule
import me.kcybulski.bricks.lobbies.LobbiesModule
import me.kcybulski.bricks.server.api.CorsConfiguration
import me.kcybulski.bricks.server.api.Server
import me.kcybulski.bricks.server.api.apikeys.ApiKeysApi
import me.kcybulski.bricks.server.api.auth.AuthInterceptor
import me.kcybulski.bricks.server.api.bots.BotsApi
import me.kcybulski.bricks.server.api.games.GamesApi
import me.kcybulski.bricks.server.api.lobbies.LobbiesListApi
import me.kcybulski.bricks.server.api.lobbies.SingleLobbyApi
import me.kcybulski.bricks.server.api.lobbies.WebsocketsRegistry
import me.kcybulski.bricks.server.healthcheck.Healthchecker
import me.kcybulski.bricks.server.healthcheck.RefreshLobbies
import me.kcybulski.bricks.server.views.gamehistory.GamesHistoryReadModel
import me.kcybulski.bricks.server.views.lobbies.LobbiesListReadModel
import me.kcybulski.bricks.server.views.lobbies.LobbyDetailsReadModel
import me.kcybulski.bricks.tournament.TournamentsModule
import me.kcybulski.nexum.eventstore.EventStore
import me.kcybulski.nexum.eventstore.inmemory.InMemoryEventStore
import java.lang.System.getenv

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
                    { eventBus, commandBus, _ -> LobbiesModule.configureInMemory(eventBus, commandBus, lobbyNameGenerator) },
                    { eventBus, _, _ -> RefreshLobbies.configure(eventBus) },
                    { _, commandBus, modules -> Healthchecker.configure(websocketsRegistry, modules[RefreshLobbies::class], commandBus) },
                    { eventBus, _, _ -> LobbiesListReadModel.configureInMemory(eventBus) },
                    { eventBus, _, _ -> LobbyDetailsReadModel.configureInMemory(eventBus) },
                    { eventBus, _, _ -> GamesHistoryReadModel.configureInMemory(eventBus) },
                    { _, _, _ -> Bots.allBots(botNameGenerator) },
                    { _, _, _ -> ApiKeys.inMemoryNoHashing() }
                )
            )

            eventsModule[Healthchecker::class].start()

            val server = Server(
                lobbiesApi = LobbiesListApi(
                    refreshLobbies = eventsModule[RefreshLobbies::class],
                    lobbiesView = eventsModule[LobbiesListReadModel::class],
                    commandBus = eventsModule.commandBus,
                    singleLobbyApi = SingleLobbyApi(
                        lobbyReadModel = eventsModule[LobbyDetailsReadModel::class],
                        bots = eventsModule[Bots::class],
                        apiKeys = eventsModule[ApiKeys::class],
                        websocketsRegistry = websocketsRegistry,
                        commandBus = eventsModule.commandBus,
                        coroutine = coroutine
                    )
                ),
                gamesApi = GamesApi(eventsModule[GamesHistoryReadModel::class]),
                botsApi = BotsApi(eventsModule[Bots::class]),
                apiKeysApi = ApiKeysApi(eventsModule[ApiKeys::class]),
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
