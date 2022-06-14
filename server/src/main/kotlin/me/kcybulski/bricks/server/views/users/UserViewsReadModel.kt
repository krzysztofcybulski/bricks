package me.kcybulski.bricks.server.views.users

import me.kcybulski.bricks.auth.UserRegistered
import me.kcybulski.bricks.events.EventBus
import me.kcybulski.bricks.server.views.Avatars

class UserViewsReadModel private constructor(
    private val repository: UsersRepository
) {

    fun find(id: String): UserView? = repository.find(id)

    private fun onUserRegistered(event: UserRegistered) {
        val user = event.apiUser
        repository.update(
            UserView(
                id = user.id,
                name = user.name,
                avatarUrl = user.avatar,
                color = "#${Avatars.color(user.id)}"
            )
        )
    }

    companion object {

        fun configureInMemory(eventBus: EventBus): UserViewsReadModel {
            val module = UserViewsReadModel(InMemoryUsersRepository())

            eventBus.subscribe(UserRegistered::class, module::onUserRegistered)

            return module
        }

    }

}
