package tech.gravyboat.skyblockrpc.config

import com.teamresourceful.resourcefulconfig.api.types.info.ResourcefulConfigLink
import com.teamresourceful.resourcefulconfig.api.types.options.TranslatableValue
import com.teamresourceful.resourcefulconfigkt.api.ConfigKt
import tech.gravyboat.skyblockrpc.Buttons
import tech.gravyboat.skyblockrpc.Element
import tech.gravyboat.skyblockrpc.SkyBlockRPC

object Config : ConfigKt("skyblock-rpc/config") {

    val config = this

    override val name = TranslatableValue("SkyBlock Discord Rich Presence")
    override val description = TranslatableValue("v${SkyBlockRPC.VERSION}")
    override val links: Array<ResourcefulConfigLink> = arrayOf(
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

    val primaryLine by draggable(Element.PURSE) {
        translation = "skyblockrpc.config.primary_line"
    }

    val secondaryLine by draggable(Element.ISLAND_AREA) {
        translation = "skyblockrpc.config.secondary_line"
    }

    val customText by string("Using SkyBlockRPC") {
        translation = "skyblockrpc.config.custom_text"
    }

    var timeBetweenRotations by int(15) {
        translation = "skyblockrpc.config.time_between_rotations"
        slider = true
        range = 5..60
    }

    val buttons by draggable(*Buttons.entries.toTypedArray()) {
        translation = "skyblockrpc.config.buttons"
    }

    val logo by enum(SkyBlockRPC.Logo.LOGO_TRANSPARENT) {
        translation = "skyblockrpc.config.logo"
    }
}