package tech.gravyboat.skyblockrpc

import com.google.gson.JsonObject
import com.jagrosh.discordipc.IPCClient
import com.jagrosh.discordipc.IPCListener
import com.jagrosh.discordipc.entities.RichPresence
import com.jagrosh.discordipc.entities.pipe.PipeStatus
import java.util.concurrent.CompletableFuture

private const val CLIENT_ID = 1356650867939475756

object RPCClient {

    private var client: IPCClient? = null

    fun start() {
        if (client?.status == PipeStatus.CONNECTED) return

        CompletableFuture.runAsync {
            client = IPCClient(CLIENT_ID).also {
                it.setListener(object : IPCListener {
                    override fun onClose(client: IPCClient?, json: JsonObject?) = stop()
                    override fun onDisconnect(client: IPCClient?, t: Throwable?) = stop()
                })
                it.connect()
            }
        }
    }

    fun stop() {
        if (client?.status == PipeStatus.DISCONNECTED) {
            client = null
        } else {
            client?.close()
            client = null
        }
    }

    fun updateActivity(action: RichPresence.Builder.() -> Unit) {
        client?.sendRichPresence(RichPresence.Builder().apply(action).build())
    }
}