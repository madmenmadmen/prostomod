//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.madmen.prosto.block;

import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class PoopLogBlock extends RotatedPillarBlock {
    public PoopLogBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    public boolean isLog(BlockState state) {
        return true;
    }
}