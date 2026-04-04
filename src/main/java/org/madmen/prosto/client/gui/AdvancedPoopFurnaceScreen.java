package org.madmen.prosto.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.madmen.prosto.Prosto;
import org.madmen.prosto.screen.AdvancedPoopFurnaceMenu;

public class AdvancedPoopFurnaceScreen extends AbstractContainerScreen<AdvancedPoopFurnaceMenu> {
    private static final ResourceLocation TEXTURE =
            new ResourceLocation(Prosto.MOD_ID, "textures/gui/container/advanced_poop_furnace.png");

    public AdvancedPoopFurnaceScreen(AdvancedPoopFurnaceMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);

        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;

        guiGraphics.blit(TEXTURE, x, y, 0, 0, this.imageWidth, this.imageHeight);

        if (menu.isLit()) {
            int litProgress = menu.getLitProgress();
            guiGraphics.blit(TEXTURE, x + 56, y + 36 + 12 - litProgress, 176, 12 - litProgress, 14, litProgress + 1);
        }

        int burnProgress = menu.getBurnProgress();
        guiGraphics.blit(TEXTURE, x + 79, y + 34, 176, 14, burnProgress + 1, 16);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }
}