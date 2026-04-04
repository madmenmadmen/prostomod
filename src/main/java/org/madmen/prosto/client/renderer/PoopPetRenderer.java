package org.madmen.prosto.client.renderer;

import net.minecraft.client.model.PigModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import org.madmen.prosto.Prosto;
import org.madmen.prosto.entity.PoopPetEntity;

public class PoopPetRenderer extends MobRenderer<PoopPetEntity, PigModel<PoopPetEntity>> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(Prosto.MOD_ID, "textures/entity/poop_pet.png");

    public PoopPetRenderer(EntityRendererProvider.Context context) {
        super(context, new PigModel<>(context.bakeLayer(ModelLayers.PIG)), 0.7F);
    }

    @Override
    public ResourceLocation getTextureLocation(PoopPetEntity entity) {
        return TEXTURE;
    }
}