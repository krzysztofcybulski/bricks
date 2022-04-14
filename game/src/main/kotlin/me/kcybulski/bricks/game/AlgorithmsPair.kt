package me.kcybulski.bricks.game

data class AlgorithmsPair(
    val first: Algorithm,
    val second: Algorithm
) {

    fun players() = PlayersPair(first.identity, second.identity)

    operator fun contains(identity: Identity) = first.identity == identity || second.identity == identity

    operator fun get(identity: Identity) = when (identity) {
        first.identity -> first
        second.identity -> second
        else -> throw IllegalStateException("No such algorithm")
    }

}

infix fun Algorithm.vs(algorithm: Algorithm) = AlgorithmsPair(this, algorithm)
