package org.madmen.prosto.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.madmen.prosto.block.EnergyCableBlock;
import org.madmen.prosto.block.entity.EnergyCableBlockEntity;

public class EnergyCableRenderer implements BlockEntityRenderer<EnergyCableBlockEntity> {

    public EnergyCableRenderer(BlockEntityRendererProvider.Context context) {
        // Контекст не нужен
    }

    @Override
    public void render(EnergyCableBlockEntity cable, float partialTicks, PoseStack poseStack,
                       MultiBufferSource bufferSource, int light, int overlay) {

        Level level = cable.getLevel();
        if (level == null) return;

        BlockState state = cable.getBlockState();
        if (!(state.getBlock() instanceof EnergyCableBlock)) return;

        ResourceLocation texture = new ResourceLocation("prosto", "textures/block/energy_cable.png");
        VertexConsumer consumer = bufferSource.getBuffer(RenderType.entitySolid(texture));

        // Цвет кабеля (чёрный)
        float r = 0.1f;
        float g = 0.1f;
        float b = 0.1f;

        // Если есть энергия — немного светится
        float energyFactor = Math.min(cable.getEnergyStored() / 1000.0f, 1.0f);
        if (energyFactor > 0) {
            r += energyFactor * 0.2f;
            g += energyFactor * 0.1f;
        }

        // 1. Центральный узел (всегда)
        renderCube(poseStack, consumer,
                0.375f, 0.375f, 0.375f,
                0.625f, 0.625f, 0.625f,
                r, g, b, light, overlay);

        // 2. Сегменты кабеля (только если подключены)
        if (state.getValue(EnergyCableBlock.NORTH)) {
            renderCableSegment(poseStack, consumer, Direction.NORTH, r, g, b, light, overlay);
        }
        if (state.getValue(EnergyCableBlock.SOUTH)) {
            renderCableSegment(poseStack, consumer, Direction.SOUTH, r, g, b, light, overlay);
        }
        if (state.getValue(EnergyCableBlock.EAST)) {
            renderCableSegment(poseStack, consumer, Direction.EAST, r, g, b, light, overlay);
        }
        if (state.getValue(EnergyCableBlock.WEST)) {
            renderCableSegment(poseStack, consumer, Direction.WEST, r, g, b, light, overlay);
        }
        if (state.getValue(EnergyCableBlock.UP)) {
            renderCableSegment(poseStack, consumer, Direction.UP, r, g, b, light, overlay);
        }
        if (state.getValue(EnergyCableBlock.DOWN)) {
            renderCableSegment(poseStack, consumer, Direction.DOWN, r, g, b, light, overlay);
        }
    }

    private void renderCableSegment(PoseStack poseStack, VertexConsumer consumer,
                                    Direction dir, float r, float g, float b,
                                    int light, int overlay) {

        poseStack.pushPose();
        poseStack.translate(0.5f, 0.5f, 0.5f);

        // ИНВЕРТИРОВАННЫЕ повороты:
        switch (dir) {
            case NORTH -> {
                // Если сегмент на север показывает на юг - инвертируем
                poseStack.mulPose(Axis.YP.rotationDegrees(180)); // ← ИНВЕРСИЯ
            }
            case SOUTH -> {
                // Если сегмент на юг показывает на север - инвертируем
                // Без поворота (было 180, стало 0)
            }
            case EAST -> {
                // Если правый сегмент показывает налево - инвертируем
                poseStack.mulPose(Axis.YP.rotationDegrees(90)); // Было -90, стало +90
            }
            case WEST -> {
                // Если левый сегмент показывает направо - инвертируем
                poseStack.mulPose(Axis.YP.rotationDegrees(-90)); // Было +90, стало -90
            }
            case UP -> {
                poseStack.mulPose(Axis.XP.rotationDegrees(-90));
            }
            case DOWN -> {
                poseStack.mulPose(Axis.XP.rotationDegrees(90));
            }
        }

        poseStack.translate(0, 0, 0.25f);
        poseStack.scale(0.25f, 0.25f, 0.5f);

        Matrix4f matrix = poseStack.last().pose();
        Matrix3f normal = poseStack.last().normal();

        renderCube(matrix, normal, consumer,
                -0.5f, -0.5f, -0.5f,
                0.5f, 0.5f, 0.5f,
                r, g, b, light, overlay);

        poseStack.popPose();
    }

    private void renderCube(PoseStack poseStack, VertexConsumer consumer,
                            float x1, float y1, float z1,
                            float x2, float y2, float z2,
                            float r, float g, float b,
                            int light, int overlay) {

        Matrix4f matrix = poseStack.last().pose();
        Matrix3f normal = poseStack.last().normal();
        renderCube(matrix, normal, consumer, x1, y1, z1, x2, y2, z2, r, g, b, light, overlay);
    }

    private void renderCube(Matrix4f matrix, Matrix3f normal, VertexConsumer consumer,
                            float x1, float y1, float z1,
                            float x2, float y2, float z2,
                            float r, float g, float b,
                            int light, int overlay) {

        // Нижняя грань (Y-)
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