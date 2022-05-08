package me.kcybulski.bricks.api

data class PlayersPair(
    val first: Identity,
    val second: Identity
) {

    operator fun contains(player: Identity) = first == player || second == player

    fun withFirst(player: Identity) = if(player == first) this else swap()

    infix fun not(identity: Identity) = when(identity) {
        first -> second
        second -> first
        else -> throw IllegalArgumentException("No such identity")
    }

    private fun swap() = PlayersPair(second, first)

}
