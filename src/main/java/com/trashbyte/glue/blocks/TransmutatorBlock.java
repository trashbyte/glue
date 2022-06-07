package com.trashbyte.glue.blocks;

import com.trashbyte.glue.ui.TransmutatorMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.StonecutterMenu;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.StonecutterBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;


public class TransmutatorBlock extends StonecutterBlock {
    private static final VoxelShape CUBE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
    private static final Component CONTAINER_TITLE = new TranslatableComponent("container.transmutator");

    public TransmutatorBlock(Properties p_57068_) {
        super(p_57068_);
    }

    @Override
    public VoxelShape getShape(BlockState a, BlockGetter b, BlockPos c, CollisionContext d) {
        return CUBE;
    }

    @Override
    public InteractionResult use(BlockState bs, Level level, BlockPos bpos, Player player, InteractionHand hand, BlockHitResult res) {
        if (!level.isClientSide) {
            player.openMenu(bs.getMenuProvider(level, bpos));
        }
        return InteractionResult.SUCCESS;
    }

    @Nullable
    @Override
    public MenuProvider getMenuProvider(BlockState bs, Level level, BlockPos pos) {
        return new SimpleMenuProvider(
                (cap, inv, player) -> new TransmutatorMenu(cap, inv, ContainerLevelAccess.create(level, pos)),
                CONTAINER_TITLE);
    }
}
