package dev.luciddream.client;

import dev.luciddream.LucidDreamMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid = "luciddream", bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public final class LucidDreamClientEvents {
    private static int selectedIndex = 0;

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) return;

        List<String> ids = new ArrayList<>(LucidDreamMod.getPowers().keySet());
        if (ids.isEmpty()) return;

        if (LucidDreamClient.OPEN_MENU.consumeClick()) {
            mc.setScreen(new DreamPowerScreen());
            return;
        }
        if (mc.screen != null) return;

        if (LucidDreamClient.NEXT_POWER.consumeClick()) {
            selectedIndex = (selectedIndex + 1) % ids.size();
            mc.player.displayClientMessage(net.minecraft.network.chat.Component.literal(
                "§dDream Power §7» §f" + LucidDreamMod.getPowers().get(ids.get(selectedIndex)) + " §8[" + ids.get(selectedIndex) + "]"), true);
        }
        if (LucidDreamClient.PREVIOUS_POWER.consumeClick()) {
            selectedIndex = (selectedIndex - 1 + ids.size()) % ids.size();
            mc.player.displayClientMessage(net.minecraft.network.chat.Component.literal(
                "§dDream Power §7» §f" + LucidDreamMod.getPowers().get(ids.get(selectedIndex)) + " §8[" + ids.get(selectedIndex) + "]"), true);
        }
        if (LucidDreamClient.ACTIVATE.consumeClick()) {
            sendCommand(mc.player, "dreampower use " + ids.get(selectedIndex));
        }
        if (LucidDreamClient.TELEKINESIS.consumeClick()) {
            sendCommand(mc.player, "dreampower use telekinesis");
        }
    }

    public static void activate(String id) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null) sendCommand(mc.player, "dreampower use " + id);
    }

    public static void select(String id) {
        List<String> ids = new ArrayList<>(LucidDreamMod.getPowers().keySet());
        int index = ids.indexOf(id);
        if (index >= 0) selectedIndex = index;
    }

    private static void sendCommand(LocalPlayer player, String command) {
        player.connection.sendCommand(command);
    }

    private LucidDreamClientEvents() {}
}
