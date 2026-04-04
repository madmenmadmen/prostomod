package org.madmen.prosto;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, Prosto.MOD_ID);

    public static final RegistryObject<SoundEvent> FART = SOUND_EVENTS.register("fart",
            () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(Prosto.MOD_ID, "fart")));

    public static final RegistryObject<SoundEvent> PORAKAKATE = SOUND_EVENTS.register("porakakate",
            () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(Prosto.MOD_ID, "porakakate")));

    public static void register(IEventBus eventBus) {
        SOUND_EVENTS.register(eventBus);
    }
}