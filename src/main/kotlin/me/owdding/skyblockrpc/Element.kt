package me.owdding.skyblockrpc

import me.owdding.skyblockrpc.config.Config
import tech.thatgravyboat.skyblockapi.api.area.mining.GlaciteAPI
import tech.thatgravyboat.skyblockapi.api.location.LocationAPI
import tech.thatgravyboat.skyblockapi.api.location.SkyBlockIsland
import tech.thatgravyboat.skyblockapi.api.profile.currency.CurrencyAPI
import tech.thatgravyboat.skyblockapi.api.profile.hotf.WhispersAPI
import tech.thatgravyboat.skyblockapi.api.profile.hotm.PowderAPI
import tech.thatgravyboat.skyblockapi.helpers.McPlayer
import tech.thatgravyboat.skyblockapi.utils.extentions.cleanName
import tech.thatgravyboat.skyblockapi.utils.extentions.toFormattedString

enum class Element(val example: String, val getter: () -> String) {
    PURSE("Purse: 123,456 (Motes in Rift)", {
        if (SkyBlockIsland.THE_RIFT.inIsland()) "Motes: ${CurrencyAPI.motes.toFormattedString()}"
        else "Purse: ${CurrencyAPI.purse.toFormattedString()}"
    }),
    BANK("Bank: 123,456", {
        "Bank: ${CurrencyAPI.bank.toFormattedString()}"
    }),
    BITS("Bits: 123,456", {
        "Bits: ${CurrencyAPI.bits.toFormattedString()}"
    }),
    ISLAND("Island: The End", {
        "Island: ${LocationAPI.island?.toString() ?: "Unknown"}"
    }),
    AREA("⏣ Auction House", {
        "⏣ ${LocationAPI.area.name}"
    }),
    ISLAND_AREA("The End - Auction House", {
        "${LocationAPI.island?.toString() ?: "Unknown"} - ${LocationAPI.area.name}"
    }),
    HELD_ITEM("Holding: Aspect of the End", {
        "Holding: ${McPlayer.heldItem.takeUnless { it.isEmpty }?.cleanName ?: "Nothing"}"
    }),
    POWDER(
        "Mithril Powder: 123,456",
        {
            val (name, amount) = when (LocationAPI.island) {
                SkyBlockIsland.CRYSTAL_HOLLOWS -> "Gemstone Powder" to PowderAPI.gemstone
                SkyBlockIsland.DWARVEN_MINES -> if (GlaciteAPI.inGlaciteTunnels()) "Glacite Powder" to PowderAPI.glacite else "Mithril Powder" to PowderAPI.mithril
                SkyBlockIsland.GALATEA -> "Forest Whispers" to WhispersAPI.forest
                else -> null to null
            }
            if (name == null || amount == null) "No Powder Island"
            else "$name: ${amount.toFormattedString()}"
        },
    ),
    CUSTOM_TEXT("Custom Text", {
        Config.customText
    }),
    ;

    override fun toString() = example

    companion object {
        fun getPrimaryLine() = getRotation(Config.primaryLine.toList())?.getter()
        fun getSecondaryLine() = getRotation(Config.secondaryLine.toList())?.getter()

        private fun getRotation(elements: List<Element>): Element? = runCatching {
            val index = (System.currentTimeMillis() / 1000 / Config.timeBetweenRotations) % elements.size
            elements[index.toInt()]
        }.getOrNull()
    }
}
