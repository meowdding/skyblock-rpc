package tech.gravyboat.skyblockrpc

import tech.gravyboat.skyblockrpc.config.Config
import tech.thatgravyboat.skyblockapi.api.location.LocationAPI
import tech.thatgravyboat.skyblockapi.api.profile.CurrencyAPI
import tech.thatgravyboat.skyblockapi.utils.extentions.toFormattedString

enum class Element(val example: String, val getter: () -> String) {
    PURSE("Purse: 123,456", {
        "Purse: ${CurrencyAPI.purse.toFormattedString()}"
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
    CUSTOM_TEXT("Custom Text", {
        Config.customText
    }),
    ROTATIONAL_1("Rotate between multiple elements", {
        getRotation(Config.rotational1.toList()).getter()
    }),
    ROTATIONAL_2("Rotate between multiple elements", {
        getRotation(Config.rotational2.toList()).getter()
    }),
    ;

    override fun toString() = example

    companion object {
        val defaultElements = setOf(PURSE, BITS, BANK, ISLAND, AREA)

        private fun getRotation(elements: List<Element>): Element {
            val elements = elements.filter { it != ROTATIONAL_1 && it != ROTATIONAL_2 }
            val index = (System.currentTimeMillis() / 1000 / Config.timeBetweenRotations) % elements.size
            return elements[index.toInt()]
        }
    }
}