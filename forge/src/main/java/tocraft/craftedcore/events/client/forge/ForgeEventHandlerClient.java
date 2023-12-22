package tocraft.craftedcore.events.client.forge;

import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import tocraft.craftedcore.events.client.ClientGuiEvents;
import tocraft.craftedcore.events.client.ClientPlayerEvents;
import tocraft.craftedcore.events.client.ClientTickEvents;

@OnlyIn(Dist.CLIENT)
public class ForgeEventHandlerClient {
	@SubscribeEvent(priority = EventPriority.HIGH)
    public static void event(ClientPlayerNetworkEvent.LoggedInEvent event) {
        ClientPlayerEvents.CLIENT_PLAYER_JOIN.invoker().join(event.getPlayer());
    }
	
	@SubscribeEvent(priority = EventPriority.HIGH)
    public static void event(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.START)
            ClientTickEvents.CLIENT_PRE.invoker().tick(Minecraft.getInstance());
        else if (event.phase == TickEvent.Phase.END)
            ClientTickEvents.CLIENT_POST.invoker().tick(Minecraft.getInstance());
    }
	
	@SubscribeEvent(priority = EventPriority.HIGH)
    public static void eventRenderGameOverlayEvent(RenderGameOverlayEvent.Post event) {
        ClientGuiEvents.RENDER_HUD.invoker().renderHud(event.getMatrixStack(), event.getPartialTicks());
    }
	
	@SubscribeEvent(priority = EventPriority.HIGH)
    public static void event(ClientPlayerNetworkEvent.LoggedOutEvent event) {
        ClientPlayerEvents.CLIENT_PLAYER_QUIT.invoker().quit(event.getPlayer());
    }
}
