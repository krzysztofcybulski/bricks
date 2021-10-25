package me.kcybulski.bricks.game

data class PlayersPair(
    val first: Identity,
    val second: Identity
) {

    fun starting(player: Identity) = if(first == player) this else swap()

    private fun swap() = PlayersPair(first, second)

    operator fun contains(player: Identity) = first == player || second == player

    infix fun not(identity: Identity) = when(identity) {
        first -> second
        second -> first
        else -> throw IllegalArgumentException("No such identity")
    }
}
