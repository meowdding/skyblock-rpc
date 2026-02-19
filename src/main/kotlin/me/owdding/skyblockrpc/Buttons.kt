package me.owdding.skyblockrpc

import tech.thatgravyboat.skyblockapi.helpers.McPlayer

private const val SOURCE = "?utm_source=SkyBlockRPC"

enum class Buttons(val label: String, val url: Lazy<String>) {
    MODRINTH("Modrinth", lazyOf("https://modrinth.com/mod/skyblock-rpc$SOURCE")),
    SKY_CRYPT("SkyCrypt", lazy { "https://sky.shiiyu.moe/stats/${McPlayer.name}$SOURCE" }),
    ELITE_BOT("EliteBot", lazy { "https://elitebot.dev/@${McPlayer.name}$SOURCE" }),
    ;

    override fun toString() = label
}
