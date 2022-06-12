package me.kcybulski.bricks.server.views.users

interface UsersRepository {

    fun update(userView: UserView)

    fun find(id: String): UserView?

}

class InMemoryUsersRepository : UsersRepository {

    private val memory: MutableMap<String, UserView> = mutableMapOf()

    override fun update(userView: UserView) {
        memory[userView.id] = userView
    }

    override fun find(id: String): UserView? = memory[id]

}
