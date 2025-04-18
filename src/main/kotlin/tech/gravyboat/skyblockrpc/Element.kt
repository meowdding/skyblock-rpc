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
    ROTATIONAL_1("Rotate between multiple elements", {
        "todo"
    }),
    ROTATIONAL_2("Rotate between multiple elements", {
        "todo"
    }),
    CUSTOM_TEXT("Custom Text", {
        Config.customText
    }),
    ;

    override fun toString() = example
}