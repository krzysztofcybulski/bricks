package me.kcybulski.bricks.server.views.gamehistory

import java.util.UUID

interface GameHistoryRepository {

    fun update(gameHistoryView: GameHistoryView)

    fun find(id: UUID): GameHistoryView?

}

class InMemoryGameHistoryRepository : GameHistoryRepository {

    private val memory: MutableMap<UUID, GameHistoryView> = mutableMapOf()

    override fun update(gameHistoryView: GameHistoryView) {
        memory[gameHistoryView.id] = gameHistoryView
    }

    override fun find(id: UUID): GameHistoryView? = memory[id]

}