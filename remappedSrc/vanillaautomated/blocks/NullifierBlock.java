package vanillaautomated.blocks;

import net.fabricmc.fabric.api.container.ContainerProviderRegistry;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import vanillaautomated.VanillaAutomated;
import vanillaautomated.VanillaAutomatedBlocks;
import vanillaautomated.blockentities.NullifierBlockEntity;

public class NullifierBlock extends BlockWithEntity {
    public NullifierBlock(Settings settings) {
        super(settings);
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
        if (itemStack.hasCustomName()) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof NullifierBlockEntity) {
                ((NullifierBlockEntity) blockEntity).setCustomName(itemStack.getName());
            }
        }
    }

    @Override
    public void onBlockRemoved(BlockState state, World world, BlockPos pos, BlockState newState, boolean notify) {
        if (!state.isOf(newState.getBlock())) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof NullifierBlockEntity) {
                world.updateComparators(pos, this);
            }
        }

        super.onBlockRemoved(state, world, pos, newState, notify);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.isClient) return ActionResult.PASS;
        BlockEntity be = world.getBlockEntity(pos);
        if (be != null && be instanceof NullifierBlockEntity) {
            ContainerProviderRegistry.INSTANCE.openContainer(new Identifier(VanillaAutomated.prefix, "nullifier"), player, (packetByteBuf -> {
                packetByteBuf.writeBlockPos(pos);
                packetByteBuf.writeText(((NullifierBlockEntity) be).getDisplayName());
            } ));
            player.incrementStat(VanillaAutomatedBlocks.interact_with_nullifier);
        }

        return ActionResult.SUCCESS;
    }

    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public BlockEntity createBlockEntity(BlockView world) {
        return new NullifierBlockEntity();
    }
}
