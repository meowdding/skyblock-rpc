package me.owdding.skyblockrpc.rpc

import com.google.gson.JsonObject
import com.jagrosh.discordipc.IPCClient
import com.jagrosh.discordipc.IPCListener
import com.jagrosh.discordipc.entities.Packet
import com.jagrosh.discordipc.entities.RichPresence
import com.jagrosh.discordipc.entities.User
import com.jagrosh.discordipc.entities.pipe.PipeStatus
import me.owdding.skyblockrpc.SkyBlockRPC
import me.owdding.skyblockrpc.config.Config
import java.util.concurrent.CompletableFuture

object RPCClient {

    private var client: IPCClient? = null

    fun start() {
        if (client != null) return

        client = IPCClient(Config.clientId.toLong())

        CompletableFuture.runAsync {
            try {
                client?.setListener(
                    object : IPCListener {
                        override fun onPacketSent(client: IPCClient, packet: Packet) {
                            if (Config.debug) SkyBlockRPC.info("Send: $packet")
                        }

                        override fun onPacketReceived(client: IPCClient, packet: Packet) {
                            if (Config.debug) SkyBlockRPC.info("Received: $packet")
                        }

                        override fun onActivityJoin(client: IPCClient, secret: String) {}
                        override fun onActivitySpectate(client: IPCClient, secret: String) {}
                        override fun onActivityJoinRequest(client: IPCClient, secret: String, user: User) {}
                        override fun onReady(client: IPCClient) {}
                        override fun onClose(client: IPCClient?, json: JsonObject?) = stop()
                        override fun onDisconnect(client: IPCClient?, t: Throwable?) = stop()
                    },
                )
                client?.connect()
            } catch (e: Throwable) {
                SkyBlockRPC.error("Failed to connect to Discord RPC", e)
                stop()
            }
        }
    }

    fun stop() {
        if (client == null) return

        if (client?.status == PipeStatus.DISCONNECTED) {
            client = null
            SkyBlockRPC.warn("Stopping while already disconnected")
        } else {
            client?.close()
            client = null
            SkyBlockRPC.info("Stopping client connection")
        }
    }

    fun updateActivity(action: RichPresence.Builder.() -> Unit) {
        if (client?.status == PipeStatus.CONNECTED) {
            client?.sendRichPresence(RichPresence.Builder().apply(action).build())
        }
    }
}
