package dev.luciddream.client;

import dev.luciddream.LucidDreamMod;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class DreamPowerScreen extends Screen {
    private int page = 0;
    private static final int PAGE_SIZE = 8;
    private List<Map.Entry<String, String>> powers = new ArrayList<>();

    public DreamPowerScreen() {
        super(Component.literal("Lucid Dream — Power Deck"));
        powers = new ArrayList<>(LucidDreamMod.getPowers().entrySet());
    }

    @Override
    protected void init() {
        clearWidgets();
        int columns = this.width >= 520 ? 2 : 1;
        int buttonWidth = columns == 2 ? Math.min(220, (this.width - 36) / 2) : Math.min(280, this.width - 24);
        int gap = 8;
        int rows = (int) Math.ceil((double) PAGE_SIZE / columns);
        int totalWidth = columns * buttonWidth + (columns - 1) * gap;
        int startX = (this.width - totalWidth) / 2;
        int startY = Math.max(42, (this.height - (rows * 24 + 42)) / 2);
        int start = page * PAGE_SIZE;
        int end = Math.min(start + PAGE_SIZE, powers.size());

        for (int i = start; i < end; i++) {
            final String id = powers.get(i).getKey();
            final String name = powers.get(i).getValue();
            int slot = i - start;
            int col = slot / rows;
            int row = slot % rows;
            int x = startX + col * (buttonWidth + gap);
            int y = startY + row * 24;
            addRenderableWidget(Button.builder(Component.literal(name), b -> {
                LucidDreamClientEvents.select(id);
                LucidDreamClientEvents.activate(id);
                onClose();
            }).bounds(x, y, buttonWidth, 20).build());
        }

        int bottomY = Math.min(this.height - 28, startY + rows * 24 + 6);
        addRenderableWidget(Button.builder(Component.literal("‹ Previous"), b -> {
            page = Math.max(0, page - 1);
            init();
        }).bounds(this.width / 2 - 148, bottomY, 92, 20).build());
        addRenderableWidget(Button.builder(Component.literal("Next ›"), b -> {
            page = Math.min((powers.size() - 1) / PAGE_SIZE, page + 1);
            init();
        }).bounds(this.width / 2 - 48, bottomY, 92, 20).build());
        addRenderableWidget(Button.builder(Component.literal("Close"), b -> onClose())
            .bounds(this.width / 2 + 52, bottomY, 92, 20).build());
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics);
        graphics.drawCenteredString(font, "✦ LUCID DREAM POWER DECK ✦", width / 2, 14, 0xD7A7FF);
        graphics.drawCenteredString(font, "Tap a power to cast it • K opens this deck • R casts selected • T telekinesis",
            width / 2, 27, 0xDAD2E8);
        graphics.drawCenteredString(font, "Page " + (page + 1) + " / " + Math.max(1, (powers.size() + PAGE_SIZE - 1) / PAGE_SIZE),
            width / 2, height - 14, 0xA99AB8);
        super.render(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
