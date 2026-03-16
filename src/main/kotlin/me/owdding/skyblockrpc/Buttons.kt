package me.owdding.skyblockrpc

import com.jagrosh.discordipc.entities.RichPresenceButton
import tech.thatgravyboat.skyblockapi.helpers.McPlayer

enum class Buttons(val label: String, private val urlProvider: () -> String) {
    MODRINTH("Modrinth", { "https://modrinth.com/mod/skyblock-rpc" }),
    SKY_CRYPT("SkyCrypt", { "https://sky.shiiyu.moe/stats/${McPlayer.name}" }),
    ELITE_BOT("EliteSkyBlock", { "https://eliteskyblock.com/@${McPlayer.name}" }), // TODO: rename enum entry
    ;

    val url: String by lazy { "${urlProvider()}?utm_source=SkyBlockRPC" }

    fun toButton() = RichPresenceButton(url, label)
    override fun toString() = label
}
