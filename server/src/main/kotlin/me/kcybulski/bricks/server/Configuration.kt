package me.kcybulski.bricks.server

import com.github.javafaker.Faker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.newSingleThreadContext
import me.kcybulski.bricks.auth.ApiKeys
import me.kcybulski.bricks.bots.Bots
import me.kcybulski.bricks.events.CommandBus
import me.kcybulski.bricks.events.EventBus
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
import me.kcybulski.bricks.server.lobby.RefreshLobbies
import me.kcybulski.bricks.tournament.TournamentsModule
import me.kcybulski.nexum.eventstore.EventStore
import me.kcybulski.nexum.eventstore.inmemory.InMemoryEventStore
import java.lang.System.getenv

data class Configuration internal constructor(
    val eventStore: EventStore,
    val eventBus: EventBus,
    val commandBus: CommandBus,
    val refreshLobbies: RefreshLobbies,
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

            val eventBus = EventBus(eventStore, CoroutineScope(newSingleThreadContext("eventBus")))
            val commandBus = CommandBus(CoroutineScope(newSingleThreadContext("commandBus")))

            LobbiesModule.configureInMemory(commandBus, eventBus, lobbyNameGenerator)
            val lobbiesView = SimpleLobbiesView.inMemory(eventBus)

            TournamentsModule.configure(eventBus, commandBus)

            val refreshLobbies = RefreshLobbies(eventStore, CoroutineScope(newSingleThreadContext("refresh")))

            val gameHistories = GameHistoriesFacade(eventStore)
            val bots = Bots.allBots(botNameGenerator)

            val apiKeys = ApiKeys.inMemoryNoHashing()

            val server = Server(
                lobbiesApi = LobbiesListApi(
                    gameHistories = gameHistories,
                    refreshLobbies = refreshLobbies,
                    lobbiesView = lobbiesView,
                    commandBus = commandBus,
                    singleLobbyApi = LobbyApi(
                        gameHistories = gameHistories,
                        lobbiesView = lobbiesView,
                        bots = bots,
                        apiKeys = apiKeys,
                        websocketsRegistry = WebsocketsRegistry(),
                        commandBus = commandBus,
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
                eventStore = eventStore,
                eventBus = eventBus,
                commandBus = commandBus,
                refreshLobbies = refreshLobbies,
                server = server
            )
        }

    }

}
