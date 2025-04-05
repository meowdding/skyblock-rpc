package tech.gravyboat.skyblockrpc

import net.fabricmc.api.ModInitializer
import tech.gravyboat.skyblockrpc.rpc.RPCClient
import tech.thatgravyboat.skyblockapi.api.SkyBlockAPI
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.base.predicates.OnlyOnSkyBlock
import tech.thatgravyboat.skyblockapi.api.events.base.predicates.TimePassed
import tech.thatgravyboat.skyblockapi.api.events.time.TickEvent

object SkyBlockRPC : ModInitializer {

    override fun onInitialize() {
        SkyBlockAPI.eventBus.register(this)

        RPCClient.start()
    }

    @Subscription
    @TimePassed("8s")
    @OnlyOnSkyBlock
    fun onTick(event: TickEvent) {
        val line1 = "Island: \${island}"
        val line2 = "A".repeat(50)

        RPCClient.updateActivity {
            setDetails(Placeholders.parse(line1))
            setState(Placeholders.parse(line2))
            setLargeImage("logo")
        }
    }
}