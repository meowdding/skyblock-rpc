package me.owdding.skyblockrpc

import com.mojang.brigadier.arguments.StringArgumentType
import com.teamresourceful.resourcefulconfig.api.client.ResourcefulConfigScreen
import com.teamresourceful.resourcefulconfig.api.loader.Configurator
import me.owdding.lib.utils.MeowddingUpdateChecker
import me.owdding.skyblockrpc.config.Config
import me.owdding.skyblockrpc.events.RegisterRpcCommandsEvent
import me.owdding.skyblockrpc.rpc.RPCClient
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import tech.thatgravyboat.skyblockapi.api.SkyBlockAPI
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.base.predicates.TimePassed
import tech.thatgravyboat.skyblockapi.api.events.time.TickEvent
import tech.thatgravyboat.skyblockapi.api.location.LocationAPI
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.utils.extentions.currentInstant
import tech.thatgravyboat.skyblockapi.utils.text.CommonText
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.Text.send
import tech.thatgravyboat.skyblockapi.utils.text.TextBuilder.append
import tech.thatgravyboat.skyblockapi.utils.text.TextColor
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.color
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.hover
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.url

object SkyBlockRPC : ClientModInitializer, Logger by LoggerFactory.getLogger("SkyBlockRPC") {

    const val MOD_ID = "skyblockrpc"
    val SELF = FabricLoader.getInstance().getModContainer(MOD_ID).get()
    val VERSION: String = SELF.metadata.version.friendlyString

    val prefix = Text.join(
        Text.of("[", TextColor.GRAY),
        Text.of("SbRPC", TextColor.AQUA),
        Text.of("] ", TextColor.GRAY),
    )

    val configurator = Configurator(MOD_ID)

    var skyblockJoin: Long? = null
    var lastUpdate = currentInstant()

    override fun onInitializeClient() {
        Config.register(configurator)
        MeowddingUpdateChecker("qESHWJ0N", SELF, ::sendUpdateMessage)
        SkyBlockAPI.eventBus.register(this)

        ClientCommandRegistrationCallback.EVENT.register { dispatcher, context ->
            RegisterRpcCommandsEvent(dispatcher, context).post(SkyBlockAPI.eventBus)
        }
        ClientLifecycleEvents.CLIENT_STOPPING.register {
            RPCClient.stop()
        }
    }

    @Subscription
    @TimePassed("1s")
    fun onTick(event: TickEvent) {
        if (!LocationAPI.isOnSkyBlock) {
            RPCClient.stop()
            skyblockJoin = null
            return
        }

        if (skyblockJoin == null) {
            skyblockJoin = System.currentTimeMillis()
        }

        RPCClient.start()

        RPCClient.updateActivity()
    }

    enum class Logo(val id: String, val displayName: String) {
        LOGO_SKY("logo_sky", "Logo with a Sky"),
        LOGO_TRANSPARENT("logo_transparent", "Logo without a Sky"),
        LOGO_TRANS("logo_trans", "Logo with a transgender flag"),
        ;

        override fun toString() = displayName
    }

    fun sendUpdateMessage(link: String, current: String, new: String) {
        fun MutableComponent.withLink() = this.apply {
            this.url = link
            this.hover = Text.of(link, TextColor.GRAY)
        }

        McClient.runNextTick {
            CommonText.EMPTY.send()
            Text.join(
                "New version found! (",
                Text.of(current, TextColor.RED),
                Text.of(" -> ", TextColor.GRAY),
                Text.of(new, TextColor.GREEN),
                ")",
            ).withLink().sendWithPrefix()
            Text.of("Click to download.").withLink().sendWithPrefix()
            CommonText.EMPTY.send()
        }
    }

    fun Component.sendWithPrefix() = Text.join(prefix, this).send()

    @Subscription
    fun onRegisterCommands(event: RegisterRpcCommandsEvent) {
        event.registerWithCallback("start") {
            RPCClient.start()
            Text.of("Started RPC").withColor(TextColor.GREEN).sendWithPrefix()
        }

        event.registerWithCallback("stop") {
            RPCClient.stop()
            Text.of("Stopped RPC").withColor(TextColor.RED).sendWithPrefix()
        }

        event.register("text") {
            thenCallback("text", StringArgumentType.greedyString()) {
                Config.customText = getArgument("text", String::class.java)
                Text.of("Set custom text to: ") {
                    color = TextColor.GRAY
                    append("\"${Config.customText}\"") {
                        color = TextColor.AQUA
                    }
                }.sendWithPrefix()
            }
        }

        event.registerBaseCallback {
            McClient.setScreenAsync { ResourcefulConfigScreen.getFactory(MOD_ID).apply(null) }
        }
    }
}
