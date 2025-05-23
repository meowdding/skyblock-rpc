package tech.gravyboat.skyblockrpc

import com.teamresourceful.resourcefulconfig.api.client.ResourcefulConfigScreen
import com.teamresourceful.resourcefulconfig.api.loader.Configurator
import net.fabricmc.api.ModInitializer
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import tech.gravyboat.skyblockrpc.config.Config
import tech.gravyboat.skyblockrpc.rpc.RPCClient
import tech.thatgravyboat.skyblockapi.api.SkyBlockAPI
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.base.predicates.OnlyOnSkyBlock
import tech.thatgravyboat.skyblockapi.api.events.base.predicates.TimePassed
import tech.thatgravyboat.skyblockapi.api.events.misc.RegisterCommandsEvent
import tech.thatgravyboat.skyblockapi.api.events.profile.ProfileChangeEvent
import tech.thatgravyboat.skyblockapi.api.events.time.TickEvent
import tech.thatgravyboat.skyblockapi.helpers.McClient

object SkyBlockRPC : ModInitializer, Logger by LoggerFactory.getLogger("SkyBlockRPC") {

    val configurator = Configurator("sbrpc")

    override fun onInitialize() {
        Config.register(configurator)

        SkyBlockAPI.eventBus.register(this)

        RPCClient.start()
    }

    @Subscription
    fun onRegisterCommands(event: RegisterCommandsEvent) {
        event.registerWithCallback("sbrpc") {
            McClient.tell {
                McClient.setScreen(ResourcefulConfigScreen.getFactory("sbrpc").apply(null))
            }
        }
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
            setDetails(Element.getRotation(Config.line1.toList()).getter())
            setState(Element.getRotation(Config.line2.toList()).getter())
            setLargeImage("logo")
            setStartTimestamp(skyblockJoin)
            Config.buttons.take(2).forEach {
                addButton(it.toButton())
            }
        }
    }
}