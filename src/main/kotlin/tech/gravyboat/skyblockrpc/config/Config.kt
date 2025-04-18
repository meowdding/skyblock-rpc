package tech.gravyboat.skyblockrpc.config

import com.teamresourceful.resourcefulconfig.api.types.info.ResourcefulConfigLink
import com.teamresourceful.resourcefulconfig.api.types.options.TranslatableValue
import com.teamresourceful.resourcefulconfigkt.api.ConfigKt
import tech.gravyboat.skyblockrpc.Element

object Config : ConfigKt("skyblock-rpc/config") {

    override val name get() = TranslatableValue("SkyBlock Discord Rich Presence")
    override val description get() = TranslatableValue("Making Modern Pv'able")
    override val links: Array<ResourcefulConfigLink>
        get() = arrayOf(
            ResourcefulConfigLink.create(
                "https://discord.gg/FsRc2GUwZR",
                "discord",
                TranslatableValue("Discord"),
            ),
            ResourcefulConfigLink.create(
                "TODO :3",
                "modrinth",
                TranslatableValue("Modrinth"),
            ),
            ResourcefulConfigLink.create(
                "https://github.com/meowdding/skyblock-rpc",
                "code",
                TranslatableValue("GitHub"),
            ),
        )

    val line1 by enum(Element.PURSE) {
        name = TranslatableValue("Line 1")
        description = TranslatableValue("Line 1 description")
    }

    val line2 by enum(Element.ISLAND_AREA) {
        name = TranslatableValue("Line 1")
        description = TranslatableValue("Line 1 description")
    }

    val customText by string("Using SkyBlockRPC") {
        name = TranslatableValue("Custom Text")
        description = TranslatableValue("Custom Text description")
    }
}