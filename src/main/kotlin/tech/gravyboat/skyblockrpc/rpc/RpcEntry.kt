package tech.gravyboat.skyblockrpc.rpc

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import tech.gravyboat.skyblockrpc.Placeholders
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

data class RpcEntry(
    val weight: Int,
    val text: String,
    val duration: Duration
) {

    val parsed: String get() = Placeholders.Companion.parse(this.text)

    companion object {

        val CODEC = RecordCodecBuilder.create {
            it.group(
                Codec.INT.fieldOf("weight").forGetter(RpcEntry::weight),
                Codec.STRING.fieldOf("text").forGetter(RpcEntry::text),
                Codec.LONG.xmap({ it.milliseconds }, { it.inWholeMilliseconds }).fieldOf("duration")
                    .forGetter(RpcEntry::duration)
            ).apply(it, ::RpcEntry)
        }
    }
}