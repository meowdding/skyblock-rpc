package tech.gravyboat.skyblockrpc.config

import com.teamresourceful.resourcefulconfig.api.types.info.ResourcefulConfigLink
import com.teamresourceful.resourcefulconfig.api.types.options.TranslatableValue
import com.teamresourceful.resourcefulconfigkt.api.ConfigKt

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
}