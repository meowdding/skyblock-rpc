package tech.gravyboat.skyblockrpc

import com.google.gson.JsonObject
import com.mojang.serialization.JsonOps
import com.teamresourceful.resourcefullib.common.collections.WeightedCollection
import com.teamresourceful.resourcefullib.common.lib.Constants
import net.fabricmc.loader.api.FabricLoader
import java.nio.file.Files

object Config {

    private val CONFIG = FabricLoader.getInstance().configDir.resolve("skyblockrpcc.json")
    private val CODEC = RpcEntry.CODEC.listOf().fieldOf("entries").codec()
    private val entries = WeightedCollection<RpcEntry>()

    fun load() {
        runCatching {
            val json = Constants.GSON.fromJson(Files.readString(CONFIG), JsonObject::class.java)
            val entries = CODEC.parse(JsonOps.INSTANCE, json).getOrThrow()
            Config.entries.clear()
            entries.forEach { entry -> Config.entries.add(entry.weight.toDouble(), entry) }
        }
    }

    fun save() {
        runCatching {
            val json = CODEC.encodeStart(JsonOps.INSTANCE, entries.toList()).getOrThrow()
            Files.writeString(CONFIG, Constants.GSON.toJson(json))
        }
    }
}