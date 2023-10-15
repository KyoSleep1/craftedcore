package tocraft.craftedcore.fabric.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.network.Connection;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import tocraft.craftedcore.events.common.PlayerEvents;

@Mixin(PlayerList.class)
public class MixinPlayerList {
	
    @Inject(method = "placeNewPlayer", at = @At("RETURN"))
    private void placeNewPlayer(Connection connection, ServerPlayer serverPlayer, CallbackInfo ci) {
        PlayerEvents.PLAYER_JOIN.invoker().join(serverPlayer);
    }
}
