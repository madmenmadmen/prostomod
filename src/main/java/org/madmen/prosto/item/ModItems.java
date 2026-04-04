package org.madmen.prosto.item;

import java.util.List;
import java.util.Objects;
import net.minecraft.advancements.Advancement;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.ArmorItem.Type;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.Nullable;
import org.madmen.prosto.Prosto;
import org.madmen.prosto.entity.ModEntities;
import org.madmen.prosto.entity.PoopPetEntity;
import org.madmen.prosto.fluid.ModFluids;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS;
    public static final RegistryObject<Item> POOP;
    public static final RegistryObject<Item> GOLDEN_POOP;
    public static final RegistryObject<Item> POOP_DYNAMITE;
    public static final RegistryObject<Item> POOP_TRAP;
    public static final RegistryObject<Item> POOP_PET_ITEM;
    public static final RegistryObject<Item> DIAMOND_POOP;
    public static final RegistryObject<Item> LIQUID_POOP_BUCKET;
    public static final RegistryObject<Item> RUBY_POOP;
    public static final RegistryObject<Item> RUBY;
    public static final RegistryObject<Item> POOP_HELMET;
    public static final RegistryObject<Item> POOP_CHESTPLATE;
    public static final RegistryObject<Item> POOP_LEGGINGS;
    public static final RegistryObject<Item> POOP_BOOTS;
    public static final RegistryObject<Item> POOP_SWORD;
    public static final RegistryObject<Item> POOP_PICKAXE;
    public static final RegistryObject<Item> POOP_AXE;
    public static final RegistryObject<Item> POOP_SHOVEL;
    public static final RegistryObject<Item> POOP_HOE;
    public static final RegistryObject<Item> UNIQUE_POOP;
    public static final RegistryObject<Item> POOP_HAMMER;
    public static final RegistryObject<Item> POOP_DRILL;
    public static final RegistryObject<Item> POOP_SAW;
    public static final RegistryObject<Item> ENERGY_CORE;
    public static final RegistryObject<Item> BIOFUEL_BUCKET;

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }

    static {
        ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Prosto.MOD_ID);

        POOP = ITEMS.register("poop", () -> new Item((new Item.Properties()).stacksTo(64)));

        GOLDEN_POOP = ITEMS.register("golden_poop", () -> new Item(new Item.Properties()));

        POOP_DYNAMITE = ITEMS.register("poop_dynamite", () -> new Item((new Item.Properties()).stacksTo(16)) {
            @Override
            public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
                return InteractionResultHolder.pass(player.getItemInHand(hand));
            }
        });

        POOP_TRAP = ITEMS.register("poop_trap", () -> new Item((new Item.Properties()).stacksTo(16)));

        POOP_PET_ITEM = ITEMS.register("poop_pet_item", () -> new Item((new Item.Properties()).stacksTo(1)) {
            @Override
            public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
                ItemStack itemStack = player.getItemInHand(hand);
                if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer) {
                    PoopPetEntity pet = new PoopPetEntity((EntityType<? extends PoopPetEntity>)ModEntities.POOP_PET.get(), level);
                    pet.moveTo(player.getX(), player.getY(), player.getZ());
                    pet.setYRot(player.getYRot());
                    pet.setXRot(player.getXRot());
                    level.addFreshEntity(pet);

                    Advancement advancement = ((MinecraftServer)Objects.requireNonNull(player.getServer()))
                            .getAdvancements()
                            .getAdvancement(new ResourceLocation("prosto", "poop_pet"));
                    if (advancement != null) {
                        serverPlayer.getAdvancements().award(advancement, "summon_pet");
                    }

                    itemStack.shrink(1);
                }

                return InteractionResultHolder.sidedSuccess(itemStack, level.isClientSide());
            }
        });

        DIAMOND_POOP = ITEMS.register("diamond_poop", () -> new Item(new Item.Properties()));

        LIQUID_POOP_BUCKET = ITEMS.register("liquid_poop_bucket",
                () -> new BucketItem(ModFluids.LIQUID_POOP, (new Item.Properties()).stacksTo(1)));

        RUBY_POOP = ITEMS.register("ruby_poop", () -> new Item(new Item.Properties()) {
            @Override
            public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
                tooltip.add(Component.literal("§cЭксклюзивная рубиновая какашка!"));
                tooltip.add(Component.literal("§6Награда за богатство"));
            }
        });

        RUBY = ITEMS.register("ruby", () -> new Item(new Item.Properties()));

        POOP_HELMET = ITEMS.register("poop_helmet",
                () -> new ArmorItem(ModArmorMaterials.POOP, Type.HELMET, new Item.Properties()));

        POOP_CHESTPLATE = ITEMS.register("poop_chestplate",
                () -> new ArmorItem(ModArmorMaterials.POOP, Type.CHESTPLATE, new Item.Properties()));

        POOP_LEGGINGS = ITEMS.register("poop_leggings",
                () -> new ArmorItem(ModArmorMaterials.POOP, Type.LEGGINGS, new Item.Properties()));

        POOP_BOOTS = ITEMS.register("poop_boots",
                () -> new ArmorItem(ModArmorMaterials.POOP, Type.BOOTS, new Item.Properties()));

        POOP_SWORD = ITEMS.register("poop_sword",
                () -> new SwordItem(ModItemTiers.POOP, 3, -2.4F, new Item.Properties()));

        POOP_PICKAXE = ITEMS.register("poop_pickaxe",
                () -> new PickaxeItem(ModItemTiers.POOP, 1, -2.8F, new Item.Properties()));

        POOP_AXE = ITEMS.register("poop_axe",
                () -> new AxeItem(ModItemTiers.POOP, 5.0F, -3.0F, new Item.Properties()));

        POOP_SHOVEL = ITEMS.register("poop_shovel",
                () -> new ShovelItem(ModItemTiers.POOP, 1.5F, -3.0F, new Item.Properties()));

        POOP_HOE = ITEMS.register("poop_hoe",
                () -> new HoeItem(ModItemTiers.POOP, -2, 0.0F, new Item.Properties()));

        UNIQUE_POOP = ITEMS.register("unique_poop", () -> new Item(new Item.Properties()) {
            @Override
            public boolean isFoil(ItemStack stack) {
                // Этот метод включает свечение как у зачарованных предметов
                return true;
            }

            @Override
            public Rarity getRarity(ItemStack stack) {
                // Rarity.EPIC дает фиолетовый цвет текста
                return Rarity.EPIC;
            }

            @Override
            public void appendHoverText(ItemStack stack, @Nullable Level level,
                                        List<Component> tooltip, TooltipFlag flag) {
                // Добавляем описание (опционально)
                tooltip.add(Component.literal("§dУникальная эксклюзивная какашка!"));
                tooltip.add(Component.literal("§5Сверкает магическим светом"));
            }
        });

        // Ведро биотоплива
        BIOFUEL_BUCKET = ITEMS.register("biofuel_bucket",
                () -> new BiofuelBucketItem(ModFluids.BIOFUEL, new Item.Properties()));

        // Ядро энергии (для крафта)
        ENERGY_CORE = ITEMS.register("energy_core",
                () -> new Item(new Item.Properties()));

        POOP_HAMMER = ITEMS.register("poop_hammer", () -> new SwordItem(
                Tiers.IRON, // или создай свой Tier
                5, // урон
                -3.2F, // скорость атаки (медленный как молот)
                new Item.Properties()
                        .durability(250) // прочность
        ) {
            @Override
            public void initializeClient(java.util.function.Consumer<net.minecraftforge.client.extensions.common.IClientItemExtensions> consumer) {
                consumer.accept(new IClientItemExtensions() {
                    @Override
                    public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                        // Это для очень сложных моделей, обычно не нужно
                        return null;
                    }
                });
            }
        });

        POOP_DRILL = ITEMS.register("poop_drill",
                () -> new Item(
                        new Item.Properties()
                                .durability(500)
                                .rarity(Rarity.UNCOMMON)
                ));

        POOP_SAW = ITEMS.register("poop_saw",
                () -> new Item(
                        new Item.Properties()
                                .durability(400)
                                .rarity(Rarity.UNCOMMON)
        ));
    }
}