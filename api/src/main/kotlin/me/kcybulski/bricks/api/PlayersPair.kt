package me.kcybulski.bricks.api

data class PlayersPair(
    val first: Identity,
    val second: Identity
) {

    private fun swap() = PlayersPair(first, second)

    operator fun contains(player: Identity) = first == player || second == player

    infix fun not(identity: Identity) = when(identity) {
        first -> second
        second -> first
        else -> throw IllegalArgumentException("No such identity")
    }

    fun names() = first.name to second.name

}
