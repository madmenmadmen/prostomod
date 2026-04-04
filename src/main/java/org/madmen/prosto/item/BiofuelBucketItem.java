package org.madmen.prosto.item;

import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluid;
import org.madmen.prosto.fluid.ModFluids;

import java.util.function.Supplier;

public class BiofuelBucketItem extends BucketItem {
    public BiofuelBucketItem(Supplier<? extends Fluid> fluid, Properties properties) {
        super(fluid, properties.craftRemainder(Items.BUCKET).stacksTo(1));
    }
}