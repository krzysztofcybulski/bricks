package me.kcybulski.bricks.events

import kotlin.reflect.KClass

class EventsModule(
    val eventBus: EventBus,
    val commandBus: CommandBus,
    initializers: List<(EventBus, CommandBus, ModulesRegistry) -> Any>
) {

    private val modules: List<Any>

    init {
        modules = initializers.fold(emptyList()) { allModules, init ->
            allModules + init(eventBus, commandBus, ModulesRegistry(allModules))
        }
    }

    operator fun <T : Any> get(clazz: KClass<T>) = modules.find { it::class == clazz } as T

}

class ModulesRegistry(
    private val modules: List<Any>
) {

    operator fun <T : Any> get(clazz: KClass<T>) = modules.find { it::class == clazz } as T

}