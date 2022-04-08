package me.kcybulski.bricks.game

import java.util.UUID
import java.util.UUID.randomUUID

sealed class Game(
    val id: UUID,
    val players: PlayersPair
)

class NewGame(
    id: UUID = randomUUID(),
    players: PlayersPair,
    val map: GameMap
) : Game(id, players) {

    fun started(startingPlayer: Identity = players.first) = InProgressGame(
        id = id,
        players = players,
        currentPlayer = startingPlayer,
        map = map
    )

    fun won(player: Identity) = WonGame(id, player, players not player)

    fun tie() = TiedGame(id, players)

}

class InProgressGame(
    id: UUID,
    players: PlayersPair,
    val currentPlayer: Identity,
    private val map: GameMap
) : Game(id, players) {

    fun placed(brick: Brick): Game = map
        .place(brick)
        .fold({ lost() }, { InProgressGame(id, players, nextPlayer, it) })

    fun lost() = WonGame(id, nextPlayer, currentPlayer)

    private val nextPlayer = players not currentPlayer

}

sealed class EndedGame(
    id: UUID,
    players: PlayersPair
) : Game(id, players)

class WonGame(
    id: UUID,
    val winner: Identity,
    val loser: Identity
) : EndedGame(id, PlayersPair(winner, loser))

class TiedGame(
    id: UUID,
    players: PlayersPair
) : EndedGame(id, players)
