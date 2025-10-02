package me.owdding.skyblockrpc

import com.teamresourceful.resourcefulconfig.api.client.ResourcefulConfigScreen
import com.teamresourceful.resourcefulconfig.api.loader.Configurator
import me.owdding.lib.utils.MeowddingUpdateChecker
import me.owdding.skyblockrpc.config.Config
import me.owdding.skyblockrpc.rpc.RPCClient
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import tech.thatgravyboat.skyblockapi.api.SkyBlockAPI
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.base.predicates.TimePassed
import tech.thatgravyboat.skyblockapi.api.events.misc.LiteralCommandBuilder
import tech.thatgravyboat.skyblockapi.api.events.misc.RegisterCommandsEvent
import tech.thatgravyboat.skyblockapi.api.events.time.TickEvent
import tech.thatgravyboat.skyblockapi.api.location.LocationAPI
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.utils.text.CommonText
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.Text.send
import tech.thatgravyboat.skyblockapi.utils.text.TextColor
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.hover
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.url

object SkyBlockRPC : ClientModInitializer, Logger by LoggerFactory.getLogger("SkyBlockRPC") {

    val MOD_ID = "skyblockrpc"
    val SELF = FabricLoader.getInstance().getModContainer(MOD_ID).get()
    val VERSION: String = SELF.metadata.version.friendlyString

    val prefix = Text.join(
        Text.of("[").withColor(TextColor.GRAY),
        Text.of("SbRPC").withColor(TextColor.AQUA),
        Text.of("] ").withColor(TextColor.GRAY),
    )

    val configurator = Configurator(MOD_ID)

    var skyblockJoin: Long? = null

    override fun onInitializeClient() {
        Config.register(configurator)
        MeowddingUpdateChecker("qESHWJ0N", SELF, ::sendUpdateMessage)
        SkyBlockAPI.eventBus.register(this)
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
            RPCClient.start()
        }

        RPCClient.updateActivity {
            setDetails(Element.getPrimaryLine())
            setState(Element.getSecondaryLine())
            setLargeImage(Config.logo.id, "Using SkyBlockRPC v$VERSION (${McClient.version})")
            setStartTimestamp(skyblockJoin!!)
            Config.buttons.take(2).forEach {
                addButton(it.toButton())
            }
        }
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
            this.hover = Text.of(link).withColor(TextColor.GRAY)
        }

        McClient.runNextTick {
            CommonText.EMPTY.send()
            Text.join(
                "New version found! (",
                Text.of(current).withColor(TextColor.RED),
                Text.of(" -> ").withColor(TextColor.GRAY),
                Text.of(new).withColor(TextColor.GREEN),
                ")",
            ).withLink().sendWithPrefix()
            Text.of("Click to download.").withLink().sendWithPrefix()
            CommonText.EMPTY.send()
        }
    }

    fun Component.sendWithPrefix() = Text.join(prefix, this).send()

    @Subscription
    fun onRegisterCommands(event: RegisterCommandsEvent) {
        val rpcCommand: (LiteralCommandBuilder.() -> Unit) = {
            callback {
                McClient.setScreenAsync { ResourcefulConfigScreen.getFactory(MOD_ID).apply(null) }
            }
        }

        event.register("sbrpc") { rpcCommand() }
        event.register("skyblockrpc") { rpcCommand() }
    }
}
