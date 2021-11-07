package me.kcybulski.bricks.gamehistory

import me.kcybulski.bricks.game.Identity
import me.kcybulski.bricks.game.PlayersPair
import me.kcybulski.bricks.gamehistory.MapBlock.Empty
import me.kcybulski.bricks.gamehistory.MapBlock.Taken
import kotlin.math.absoluteValue

object GameMapRenderer {

    fun toString(map: GameMap): String {
        val colors = playerColors(map.players)
        return playersToString(map, colors) + "\n" + blocksToString(map, colors)
    }

    private fun playerColors(players: PlayersPair): Map<Identity, String> {
        val firstColor = playerToColor(players.first, COLORS)
        val secondColor = playerToColor(players.second, COLORS - firstColor)
        return mapOf(players.first to firstColor, players.second to secondColor)
    }

    private fun playersToString(map: GameMap, colors: Map<Identity, String>): String = """
        ${colors[map.players.first]} ${map.players.first.name} (starting)
        ${colors[map.players.second]} ${map.players.second.name}
        __________
    """.trimIndent()

    private fun blocksToString(map: GameMap, colors: Map<Identity, String>) = map
        .blocks
        .joinToString("\n") { row ->
            row.joinToString("") { toString(it, colors) }
        }

    private fun toString(block: MapBlock, colors: Map<Identity, String>): String = when (block) {
        Empty -> "⬜"
        is Taken -> colors[block.owner] ?: "❓"
    }

    private fun playerToColor(identity: Identity, colors: List<String>) = colors[identity.hashCode().absoluteValue % colors.size]

    private val COLORS = listOf(
        "\uD83D\uDFE5",
        "\uD83D\uDFE7",
        "\uD83D\uDFE8",
        "\uD83D\uDFE9",
        "\uD83D\uDFE6",
        "\uD83D\uDFEA",
        "\uD83D\uDFEB"
    )

}
