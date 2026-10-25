package dev.luciddream.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.api.distmarker.Dist;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(modid = "luciddream", bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class LucidDreamClient {
    public static final KeyMapping OPEN_MENU = new KeyMapping(
        "key.luciddream.open_menu", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_K, "key.categories.luciddream");
    public static final KeyMapping ACTIVATE = new KeyMapping(
        "key.luciddream.activate", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_R, "key.categories.luciddream");
    public static final KeyMapping NEXT_POWER = new KeyMapping(
        "key.luciddream.next_power", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_RIGHT_BRACKET, "key.categories.luciddream");
    public static final KeyMapping PREVIOUS_POWER = new KeyMapping(
        "key.luciddream.previous_power", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_LEFT_BRACKET, "key.categories.luciddream");
    public static final KeyMapping TELEKINESIS = new KeyMapping(
        "key.luciddream.telekinesis", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_G, "key.categories.luciddream");

    @SubscribeEvent
    public static void registerKeys(RegisterKeyMappingsEvent event) {
        event.register(OPEN_MENU);
        event.register(ACTIVATE);
        event.register(NEXT_POWER);
        event.register(PREVIOUS_POWER);
        event.register(TELEKINESIS);
    }

    private LucidDreamClient() {}
}
