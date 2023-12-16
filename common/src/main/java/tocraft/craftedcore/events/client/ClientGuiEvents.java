package tocraft.craftedcore.events.client;

import com.mojang.blaze3d.vertex.PoseStack;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import tocraft.craftedcore.events.Event;
import tocraft.craftedcore.events.EventBuilder;

public interface ClientGuiEvents {
	
	Event<RenderHud> RENDER_HUD = EventBuilder.createLoop();

	@Environment(EnvType.CLIENT)
    interface RenderHud {
        /**
         * Invoked after the in-game hud has been rendered.
         * Equivalent to Forge's {@code RenderGameOverlayEvent.Post@ElementType#ALL} and Fabric's {@code HudRenderCallback}.
         *
         * @param graphics  The graphics context.
         * @param tickDelta The tick delta.
         */
        void renderHud(PoseStack graphics, float tickDelta);
    }
}
