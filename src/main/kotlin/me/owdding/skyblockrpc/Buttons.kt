package me.owdding.skyblockrpc

import tech.thatgravyboat.skyblockapi.helpers.McPlayer

private const val SOURCE = "?utm_source=SkyBlockRPC"

enum class Buttons(val label: String, private val urlProvider: () -> String) {
    MODRINTH("Modrinth", { "https://modrinth.com/mod/skyblock-rpc$SOURCE" }),
    SKY_CRYPT("SkyCrypt", { "https://sky.shiiyu.moe/stats/${McPlayer.name}$SOURCE" }),
    ELITE_BOT("EliteSkyBlock", { "https://eliteskyblock.com/@${McPlayer.name}$SOURCE" }), // TODO: rename enum entry
    ;

    val url: String by lazy { "${urlProvider()}?utm_source=SkyBlockRPC" }

    override fun toString() = label
}
