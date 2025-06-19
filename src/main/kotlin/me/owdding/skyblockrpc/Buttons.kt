package me.owdding.skyblockrpc

import com.jagrosh.discordipc.entities.RichPresenceButton
import tech.thatgravyboat.skyblockapi.helpers.McPlayer

enum class Buttons(val label: String, val url: Lazy<String>) {
    MODRINTH("Modrinth", lazyOf("https://modrinth.com/mod/skyblock-rpc")),
    SKY_CRYPT("SkyCrypt", lazy { "https://sky.shiiyu.moe/stats/${McPlayer.name}" }),
    ELITE_BOT("EliteBot", lazy { "https://elitebot.dev/@${McPlayer.name}" }),
    ;

    fun toButton() = RichPresenceButton(url.value, label)
    override fun toString() = label
}