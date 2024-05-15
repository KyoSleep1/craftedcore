package tocraft.craftedcore.fabric;

import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.EntitySleepEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.minecraft.commands.Commands;
import tocraft.craftedcore.event.common.CommandEvents;
import tocraft.craftedcore.event.common.PlayerEvents;
import tocraft.craftedcore.event.common.ServerLevelEvents;

public class CraftedCoreFabricEventHandler {

    public static void initialize() {
        EntitySleepEvents.ALLOW_SLEEP_TIME.register((player, sleepingPos, vanillaResult) -> PlayerEvents.ALLOW_SLEEP_TIME.invoke().allowSleepTime(player, sleepingPos, vanillaResult));
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> CommandEvents.REGISTRATION.invoke().register(dispatcher, dedicated ? Commands.CommandSelection.DEDICATED : Commands.CommandSelection.INTEGRATED));
        ServerWorldEvents.LOAD.register((server, world) -> ServerLevelEvents.LEVEL_LOAD.invoke().call(world));
        ServerWorldEvents.UNLOAD.register((server, world) -> ServerLevelEvents.LEVEL_UNLOAD.invoke().call(world));
    }
}
