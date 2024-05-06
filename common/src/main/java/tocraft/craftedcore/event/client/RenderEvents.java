package tocraft.craftedcore.event.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import tocraft.craftedcore.event.Event;
import tocraft.craftedcore.event.EventFactory;

@SuppressWarnings("unused")
@Environment(EnvType.CLIENT)
public final class RenderEvents {
    public static final Event<HUDRendering> HUD_RENDERING = EventFactory.createWithVoid();
    public static final Event<OverlayRendering> RENDER_HEALTH = EventFactory.createWithInteractionResult();
    public static final Event<OverlayRendering> RENDER_FOOD = EventFactory.createWithInteractionResult();
    public static final Event<BreathOverLayRendering> RENDER_BREATH = EventFactory.createWithInteractionResult();
    public static final Event<OverlayRendering> RENDER_MOUNT_HEALTH = EventFactory.createWithInteractionResult();

    @Environment(EnvType.CLIENT)
    @FunctionalInterface
    public interface HUDRendering {
        void render(GuiGraphics graphics, float tickDelta);
    }

    @Environment(EnvType.CLIENT)
    @FunctionalInterface
    public interface OverlayRendering {
        InteractionResult render(Player player, GuiGraphics graphics);
    }

    @Environment(EnvType.CLIENT)
    @FunctionalInterface
    public interface BreathOverLayRendering {
        InteractionResult render(Player player);
    }
}
