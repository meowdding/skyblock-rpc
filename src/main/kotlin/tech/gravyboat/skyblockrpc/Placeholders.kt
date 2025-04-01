package tech.gravyboat.skyblockrpc

import tech.thatgravyboat.skyblockapi.api.location.LocationAPI
import tech.thatgravyboat.skyblockapi.api.profile.CurrencyAPI

enum class Placeholders(
    val example: String,
    val getter: (String?) -> Any
) {
    BITS("1234", { CurrencyAPI.bits }),
    PURSE("1234", { CurrencyAPI.purse }),
    BANK("1234", { CurrencyAPI.bank }),

    ISLAND("The End", { LocationAPI.island?.toString() ?: "Unknown" }),
    AREA("Auction House", { LocationAPI.area.name }),
    ;

    fun parse(data: String?): String {
        return getter.invoke(data).toString()
    }

    companion object {

        private val placeholderRegex = Regex("\\$\\{(?<data>[^}]+)}")

        fun parse(string: String): String = string.replace(placeholderRegex) {
            val parts = it.groups["data"]?.value?.split(":", limit = 2) ?: return@replace it.value
            val placeholder = get(parts[0]) ?: return@replace it.value
            placeholder.parse(parts.getOrNull(1))
        }

        fun get(name: String): Placeholders? {
            return Placeholders.entries.find { it.name.equals(name, ignoreCase = true) }
        }
    }
}