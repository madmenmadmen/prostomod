package org.madmen.prosto.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColors;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.madmen.prosto.block.EnergyPumpBlock;
import org.madmen.prosto.block.entity.EnergyPumpBlockEntity;

public class EnergyPumpRenderer implements BlockEntityRenderer<EnergyPumpBlockEntity> {

    private final BlockRenderDispatcher blockRenderer;

    private static final ResourceLocation TEXTURE =
            new ResourceLocation("prosto", "textures/block/energy_pump.png");

    public EnergyPumpRenderer(BlockEntityRendererProvider.Context context) {
        this.blockRenderer = context.getBlockRenderDispatcher();
    }

    @Override
    public void render(EnergyPumpBlockEntity pump, float partialTicks, PoseStack poseStack,
                       MultiBufferSource bufferSource, int light, int overlay) {

        if (pump.getLevel() == null) return;

        BlockState state = pump.getBlockState();
        if (!(state.getBlock() instanceof EnergyPumpBlock)) return;

        EnergyPumpBlock.PumpMode mode = state.getValue(EnergyPumpBlock.MODE);
        Direction facing = state.getValue(EnergyPumpBlock.FACING);

        // Получаем цвет
        int color = switch (mode) {
            case PUSH -> 0xFF00FF00;
            case PULL -> 0xFFFF0000;
            case NONE -> 0xFF808080;
        };

        float r = ((color >> 16) & 0xFF) / 255.0f;
        float g = ((color >> 8) & 0xFF) / 255.0f;
        float b = (color & 0xFF) / 255.0f;

        // Добавляем свечение если есть энергия
        float energyFactor = pump.getEnergyFillRatio();
        if (energyFactor > 0 && mode != EnergyPumpBlock.PumpMode.NONE) {
            float glow = 0.5f + energyFactor * 0.5f;
            r = Math.min(r * glow, 1.0f);
            g = Math.min(g * glow, 1.0f);
            b = Math.min(b * glow, 1.0f);
        }

        VertexConsumer consumer = bufferSource.getBuffer(RenderType.entitySolid(TEXTURE));

        // Рендерим с правильной ориентацией
        renderPumpModel(poseStack, consumer, r, g, b, 1.0f, light, overlay, facing);
    }

    private void renderPumpModel(PoseStack poseStack, VertexConsumer consumer,
                                 float r, float g, float b, float a,
                                 int light, int overlay, Direction facing) {

        poseStack.pushPose();
        poseStack.translate(0.5f, 0.5f, 0.5f);

        // КОРРЕКТИРОВКА: модель сделана для WEST стороны в Blockbench
        // Поэтому нужно повернуть её согласно facing
        switch (facing) {
            case WEST -> {
                // Уже правильная ориентация
                poseStack.mulPose(Axis.YP.rotationDegrees(180)); // Поворачиваем чтобы была видна
            }
            case EAST -> {
                poseStack.mulPose(Axis.YP.rotationDegrees(0));
            }
            case NORTH -> {
                poseStack.mulPose(Axis.YP.rotationDegrees(90));
            }
            case SOUTH -> {
                poseStack.mulPose(Axis.YP.rotationDegrees(-90));
            }
            case UP -> {
                poseStack.mulPose(Axis.XP.rotationDegrees(90));
            }
            case DOWN -> {
                poseStack.mulPose(Axis.XP.rotationDegrees(-90));
            }
        }

        poseStack.translate(-0.5f, -0.5f, -0.5f);

        // Рендерим куб согласно модели
        // Размер модели: from [0, 0, 2] to [2, 11, 14]
        // Переводим в координаты блоков: [0-2]/16, [0-11]/16, [2-14]/16
        float x1 = 0.0f / 16.0f;
        float x2 = 2.0f / 16.0f;
        float y1 = 0.0f / 16.0f;
        float y2 = 11.0f / 16.0f;
        float z1 = 2.0f / 16.0f;
        float z2 = 14.0f / 16.0f;

        // Рендерим куб
        renderCube(poseStack, consumer, x1, y1, z1, x2, y2, z2, r, g, b, a, light, overlay);

        poseStack.popPose();
    }

    // Метод renderCube остаётся как у вас был
    private void renderCube(PoseStack poseStack, VertexConsumer consumer,
                            float x1, float y1, float z1,
                            float x2, float y2, float z2,
                            float r, float g, float b, float a,
                            int light, int overlay) {
        Matrix4f matrix = poseStack.last().pose();
        Matrix3f normal = poseStack.last().normal();

        consumer.vertex(matrix, x1, y1, z1).color(r, g, b, 1.0f)
                .uv(0, 0).overlayCoords(overlay).uv2(light)
                .normal(normal, 0, -1, 0).endVertex();

        consumer.vertex(matrix, x2, y1, z1).color(r, g, b, 1.0f)
                .uv(1, 0).overlayCoords(overlay).uv2(light)
                .normal(normal, 0, -1, 0).endVertex();

        consumer.vertex(matrix, x2, y1, z2).color(r, g, b, 1.0f)
                .uv(1, 1).overlayCoords(overlay).uv2(light)
                .normal(normal, 0, -1, 0).endVertex();

        consumer.vertex(matrix, x1, y1, z2).color(r, g, b, 1.0f)
                .uv(0, 1).overlayCoords(overlay).uv2(light)
                .normal(normal, 0, -1, 0).endVertex();

        // Верхняя грань (Y+)
        consumer.vertex(matrix, x1, y2, z1).color(r, g, b, 1.0f)
                .uv(0, 0).overlayCoords(overlay).uv2(light)
                .normal(normal, 0, 1, 0).endVertex();

        consumer.vertex(matrix, x1, y2, z2).color(r, g, b, 1.0f)
                .uv(0, 1).overlayCoords(overlay).uv2(light)
                .normal(normal, 0, 1, 0).endVertex();

        consumer.vertex(matrix, x2, y2, z2).color(r, g, b, 1.0f)
                .uv(1, 1).overlayCoords(overlay).uv2(light)
                .normal(normal, 0, 1, 0).endVertex();

        consumer.vertex(matrix, x2, y2, z1).color(r, g, b, 1.0f)
                .uv(1, 0).overlayCoords(overlay).uv2(light)
                .normal(normal, 0, 1, 0).endVertex();

        // Северная грань (Z-)
        consumer.vertex(matrix, x1, y1, z1).color(r, g, b, 1.0f)
                .uv(0, 0).overlayCoords(overlay).uv2(light)
                .normal(normal, 0, 0, -1).endVertex();

        consumer.vertex(matrix, x1, y2, z1).color(r, g, b, 1.0f)
                .uv(0, 1).overlayCoords(overlay).uv2(light)
                .normal(normal, 0, 0, -1).endVertex();

        consumer.vertex(matrix, x2, y2, z1).color(r, g, b, 1.0f)
                .uv(1, 1).overlayCoords(overlay).uv2(light)
                .normal(normal, 0, 0, -1).endVertex();

        consumer.vertex(matrix, x2, y1, z1).color(r, g, b, 1.0f)
                .uv(1, 0).overlayCoords(overlay).uv2(light)
                .normal(normal, 0, 0, -1).endVertex();

        // Южная грань (Z+)
        consumer.vertex(matrix, x1, y1, z2).color(r, g, b, 1.0f)
                .uv(0, 0).overlayCoords(overlay).uv2(light)
                .normal(normal, 0, 0, 1).endVertex();

        consumer.vertex(matrix, x2, y1, z2).color(r, g, b, 1.0f)
                .uv(1, 0).overlayCoords(overlay).uv2(light)
                .normal(normal, 0, 0, 1).endVertex();

        consumer.vertex(matrix, x2, y2, z2).color(r, g, b, 1.0f)
                .uv(1, 1).overlayCoords(overlay).uv2(light)
                .normal(normal, 0, 0, 1).endVertex();

        consumer.vertex(matrix, x1, y2, z2).color(r, g, b, 1.0f)
                .uv(0, 1).overlayCoords(overlay).uv2(light)
                .normal(normal, 0, 0, 1).endVertex();

        // Западная грань (X-)
        consumer.vertex(matrix, x1, y1, z1).color(r, g, b, 1.0f)
                .uv(0, 0).overlayCoords(overlay).uv2(light)
                .normal(normal, -1, 0, 0).endVertex();

        consumer.vertex(matrix, x1, y1, z2).color(r, g, b, 1.0f)
                .uv(1, 0).overlayCoords(overlay).uv2(light)
                .normal(normal, -1, 0, 0).endVertex();

        consumer.vertex(matrix, x1, y2, z2).color(r, g, b, 1.0f)
                .uv(1, 1).overlayCoords(overlay).uv2(light)
                .normal(normal, -1, 0, 0).endVertex();

        consumer.vertex(matrix, x1, y2, z1).color(r, g, b, 1.0f)
                .uv(0, 1).overlayCoords(overlay).uv2(light)
                .normal(normal, -1, 0, 0).endVertex();

        // Восточная грань (X+)
        consumer.vertex(matrix, x2, y1, z1).color(r, g, b, 1.0f)
                .uv(0, 0).overlayCoords(overlay).uv2(light)
                .normal(normal, 1, 0, 0).endVertex();

        consumer.vertex(matrix, x2, y2, z1).color(r, g, b, 1.0f)
                .uv(0, 1).overlayCoords(overlay).uv2(light)
                .normal(normal, 1, 0, 0).endVertex();

        consumer.vertex(matrix, x2, y2, z2).color(r, g, b, 1.0f)
                .uv(1, 1).overlayCoords(overlay).uv2(light)
                .normal(normal, 1, 0, 0).endVertex();

        consumer.vertex(matrix, x2, y1, z2).color(r, g, b, 1.0f)
                .uv(1, 0).overlayCoords(overlay).uv2(light)
                .normal(normal, 1, 0, 0).endVertex();
    }
}