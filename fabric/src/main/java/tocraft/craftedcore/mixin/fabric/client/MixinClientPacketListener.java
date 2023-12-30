package tocraft.craftedcore.mixin.fabric.client;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundLoginPacket;
import tocraft.craftedcore.events.client.ClientPlayerEvents;

@Mixin(ClientPacketListener.class)
public abstract class MixinClientPacketListener {
	@Shadow
    @Final
    private Minecraft minecraft;
    
    @Inject(method = "handleLogin", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Options;setServerRenderDistance(I)V", shift = At.Shift.AFTER))
    private void handleLogin(ClientboundLoginPacket packet, CallbackInfo ci) {
        ClientPlayerEvents.CLIENT_PLAYER_JOIN.invoker().join(minecraft.player);
    }
}
