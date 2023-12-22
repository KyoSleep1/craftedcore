package tocraft.craftedcore.events.common.fabric;

import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import tocraft.craftedcore.events.common.CommandEvents;
import net.minecraft.commands.Commands;

public class FabricEventHandler {

	public FabricEventHandler() {
		CommandRegistrationCallback.EVENT.register((dispatcher, b) -> CommandEvents.REGISTRATION.invoker().register(dispatcher, b ? Commands.CommandSelection.DEDICATED : Commands.CommandSelection.INTEGRATED));
	}
}
