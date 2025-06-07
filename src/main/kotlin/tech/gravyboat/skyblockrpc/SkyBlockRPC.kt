package tech.gravyboat.skyblockrpc

import com.teamresourceful.resourcefulconfig.api.client.ResourcefulConfigScreen
import com.teamresourceful.resourcefulconfig.api.loader.Configurator
import net.fabricmc.api.ModInitializer
import net.fabricmc.loader.api.FabricLoader
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import tech.gravyboat.skyblockrpc.config.Config
import tech.gravyboat.skyblockrpc.rpc.RPCClient
import tech.thatgravyboat.skyblockapi.api.SkyBlockAPI
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.base.predicates.OnlyOnSkyBlock
import tech.thatgravyboat.skyblockapi.api.events.base.predicates.TimePassed
import tech.thatgravyboat.skyblockapi.api.events.misc.LiteralCommandBuilder
import tech.thatgravyboat.skyblockapi.api.events.misc.RegisterCommandsEvent
import tech.thatgravyboat.skyblockapi.api.events.profile.ProfileChangeEvent
import tech.thatgravyboat.skyblockapi.api.events.time.TickEvent
import tech.thatgravyboat.skyblockapi.helpers.McClient

object SkyBlockRPC : ModInitializer, Logger by LoggerFactory.getLogger("SkyBlockRPC") {

    val SELF = FabricLoader.getInstance().getModContainer("skyblockrpc").get()
    val VERSION: String = SELF.metadata.version.friendlyString

    val configurator = Configurator("sbrpc")

    override fun onInitialize() {
        Config.register(configurator)

        SkyBlockAPI.eventBus.register(this)

        RPCClient.start()
    }

    @Subscription
    fun onRegisterCommands(event: RegisterCommandsEvent) {
        val rpcCommand: (LiteralCommandBuilder.() -> Unit) = {
            callback {
                McClient.setScreenAsync(ResourcefulConfigScreen.getFactory("sbrpc").apply(null))
            }
        }

        event.register("sbrpc") { rpcCommand() }
        event.register("skyblockrpc") { rpcCommand() }
    }

    var skyblockJoin = 0L

    @Subscription
    fun onProfile(event: ProfileChangeEvent) {
        skyblockJoin = System.currentTimeMillis()
    }

    @Subscription
    @TimePassed("5s")
    @OnlyOnSkyBlock
    fun onTick(event: TickEvent) {
        RPCClient.updateActivity {
            setDetails(Element.getPrimaryLine())
            setState(Element.getSecondaryLine())
            setLargeImage(Config.logo.id, "Using SkyBlockRPC v$VERSION")
            setStartTimestamp(skyblockJoin)
            Config.buttons.take(2).forEach {
                addButton(it.toButton())
            }
        }
    }

    enum class Logo(val id: String, val displayName: String) {
        LOGO_SKY("logo_sky", "Logo with a Sky"),
        LOGO_TRANSPARENT("logo_transparent", "Logo without a Sky"),
        ;

        override fun toString() = displayName
    }
}