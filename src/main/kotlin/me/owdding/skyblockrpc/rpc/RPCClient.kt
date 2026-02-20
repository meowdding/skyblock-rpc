package me.owdding.skyblockrpc.rpc

import io.github.vyfor.kpresence.ConnectionState
import io.github.vyfor.kpresence.RichClient
import io.github.vyfor.kpresence.event.DisconnectEvent
import io.github.vyfor.kpresence.event.ReadyEvent
import io.github.vyfor.kpresence.logger.ILogger
import me.owdding.skyblockrpc.Element
import me.owdding.skyblockrpc.SkyBlockRPC
import me.owdding.skyblockrpc.config.Config
import tech.thatgravyboat.skyblockapi.helpers.McClient

object RPCClient {

    private var client: RichClient? = null

    fun start() {
        if (isConnected()) return

        client = RichClient(Config.clientId.toLong()).apply {
            logger = ILogger.default()
            on<ReadyEvent> {
                SkyBlockRPC.info("RPC Connected")
            }
            on<DisconnectEvent> {
                stop()
                SkyBlockRPC.info("RPC Disconnected")
            }
            connect()
        }
    }

    fun stop() {
        if (!isConnected()) {
            client = null
        } else {
            client?.update(null)
            client?.shutdown()
            client = null
        }
    }

    fun updateActivity() {
        if (!isConnected()) return
        client?.update {
            details = Element.getPrimaryLine()
            state = Element.getSecondaryLine()

            timestamps {
                start = SkyBlockRPC.skyblockJoin
            }

            assets {
                largeImage = Config.logo.id
                largeText = "Using SkyBlockRPC v${SkyBlockRPC.VERSION} (${McClient.version})"
            }

            Config.buttons.take(2).forEach {
                button(it.label, it.url.value)
            }
        }
    }

    private fun isConnected(): Boolean = client?.connectionState == ConnectionState.SENT_HANDSHAKE
}
