package me.owdding.skyblockrpc

import me.owdding.lib.extensions.toReadableTime
import me.owdding.skyblockrpc.config.Config
import tech.thatgravyboat.skyblockapi.api.area.slayer.SlayerAPI
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.helpers.McScreen
import tech.thatgravyboat.skyblockapi.utils.text.TextProperties.stripped
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes

enum class DynamicElement(val example: String, val getter: () -> String?) {
    SLAYER(
        "Slayer Boss",
        {
            val current = SlayerAPI.progress?.current
            val max = SlayerAPI.progress?.max
            if (current != null && max != null) {
                val text = when {
                    max == 0 || current == 0 -> "Inactive!"
                    current == max -> "Complete!"
                    SlayerAPI.text != null -> SlayerAPI.text
                    else -> "$current/$max"
                }
                "Slayer: ${SlayerAPI.type?.displayName} $text"
            } else null
        },
    ),
    INVENTORY_TITLE(
        "Inventory Title",
        {
            McScreen.self?.title?.stripped?.let { "Looking at $it" }
        },
    ),
    AFK(
        "AFK Time",
        {
            McClient.self.framerateLimitTracker.latestInputTime.takeIf { it > 1.minutes.inWholeMilliseconds }?.let {
                "Afk for ${it.milliseconds.toReadableTime()}"
            }
        },
    ),
    ;

    override fun toString() = example

    companion object {
        fun getElement() = Config.dynamicPriority.firstNotNullOfOrNull { it.getter() }
    }
}
