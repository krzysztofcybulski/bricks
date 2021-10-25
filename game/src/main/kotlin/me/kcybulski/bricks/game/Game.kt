package me.kcybulski.bricks.game

sealed class Game(
    val players: PlayersPair
)

class NewGame(
    players: PlayersPair,
    val size: Int
) : Game(players) {

    fun started(startingPlayer: Identity = players.first) = InProgressGame(
        players = players,
        currentPlayer = startingPlayer,
        map = Map.of(size)
    )

    fun won(player: Identity) = WonGame(player, players not player)

    fun tie() = TiedGame(players)

}

class InProgressGame(
    players: PlayersPair,
    val currentPlayer: Identity,
    private val map: Map
) : Game(players) {

    fun placed(brick: Brick): Game = map.place(brick)
        .fold({ lost() }, { InProgressGame(players, nextPlayer, it) })

    fun lost() = WonGame(nextPlayer, currentPlayer)

    private val nextPlayer = players not currentPlayer

}

sealed class EndedGame(
    players: PlayersPair
) : Game(players)

class WonGame(
    val winner: Identity,
    val loser: Identity
) : EndedGame(PlayersPair(winner, loser))

class TiedGame(
    players: PlayersPair
) : EndedGame(players)
