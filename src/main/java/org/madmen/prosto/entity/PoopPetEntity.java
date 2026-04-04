package org.madmen.prosto.entity;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.NonTameRandomTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.madmen.prosto.item.ModItems;

public class PoopPetEntity extends TamableAnimal {
    private int poopTimer = 0;

    public PoopPetEntity(EntityType<? extends PoopPetEntity> type, Level level) {
        super(type, level);
        this.setPersistenceRequired();
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(3, new FollowOwnerGoal(this, 1.0, 10.0F, 2.0F, false));
        this.goalSelector.addGoal(4, new WaterAvoidingRandomStrollGoal(this, 1.0));
        this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Animal.class, 6.0F));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new NonTameRandomTargetGoal<>(this, Animal.class, false, null));
        this.targetSelector.addGoal(2, new NonTameRandomTargetGoal<>(this, Monster.class, false, null));
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.level().isClientSide && this.isAlive()) {
            poopTimer++;
            if (poopTimer >= 400) {
                poopTimer = 0;
                ItemStack poop = new ItemStack(ModItems.POOP.get());
                net.minecraft.world.entity.item.ItemEntity item = new net.minecraft.world.entity.item.ItemEntity(
                        this.level(),
                        this.getX(), this.getY() - 0.5, this.getZ(),
                        poop
                );
                item.setDeltaMovement(0, 0.1, 0);
                this.level().addFreshEntity(item);
            }
        }
    }

    @Override
    public PoopPetEntity getBreedOffspring(ServerLevel level, AgeableMob other) {
        return new PoopPetEntity(ModEntities.POOP_PET.get(), level);
    }
}