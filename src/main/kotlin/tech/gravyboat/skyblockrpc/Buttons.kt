package tech.gravyboat.skyblockrpc

import com.jagrosh.discordipc.entities.RichPresenceButton
import tech.thatgravyboat.skyblockapi.helpers.McPlayer

enum class Buttons(val label: String, val url: Lazy<String>) {
    GITHUB("GitHub", lazyOf("https://github.com/meowdding/skyblock-rpc")),
    SKY_CRYPT("SkyCrypt", lazy { "https://sky.shiiyu.moe/stats/${McPlayer.name}" }),
    ELITE_BOT("EliteBot", lazy { "https://elitebot.dev/@${McPlayer.name}" }),
    ;

    fun toButton() = RichPresenceButton(url.value, label)
}