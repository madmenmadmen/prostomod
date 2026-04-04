package org.madmen.prosto.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.madmen.prosto.Prosto;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, Prosto.MOD_ID);

    public static final RegistryObject<EntityType<PoopPetEntity>> POOP_PET =
            ENTITIES.register("poop_pet",
                    () -> EntityType.Builder.of(PoopPetEntity::new, MobCategory.CREATURE)
                            .sized(0.6f, 0.6f)
                            .clientTrackingRange(10)
                            .build("poop_pet")
            );
}