package me.owdding.skyblockrpc.events

import com.mojang.brigadier.CommandDispatcher
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.minecraft.commands.CommandBuildContext
import tech.thatgravyboat.skyblockapi.api.events.misc.AbstractModRegisterCommandsEvent
import tech.thatgravyboat.skyblockapi.api.events.misc.RegisterCommandsEvent

class RegisterRpcCommandsEvent(
    val dispatcher: CommandDispatcher<FabricClientCommandSource>,
    val context: CommandBuildContext,
) : AbstractModRegisterCommandsEvent(RegisterCommandsEvent(dispatcher, context), "sbrpc", "skyblockrpc")
