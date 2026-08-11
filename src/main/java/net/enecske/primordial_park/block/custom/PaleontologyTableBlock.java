package net.enecske.primordial_park.block.custom;

import com.mojang.serialization.MapCodec;
import net.enecske.primordial_park.block.ModBlockEntities;
import net.enecske.primordial_park.block.entity.PaleontologyTableBlockEntity;
import net.enecske.primordial_park.inventory.PaleontologyTableMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PaleontologyTableBlock extends BaseEntityBlock {
    public static final MapCodec<PaleontologyTableBlock> CODEC = simpleCodec(PaleontologyTableBlock::new);

    public PaleontologyTableBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected @NotNull MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new PaleontologyTableBlockEntity(pos, state);
    }

    @Override
    protected void onRemove(BlockState state, @NotNull Level level, @NotNull BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (state.getBlock() != newState.getBlock()) {
            if (level.getBlockEntity(pos) instanceof PaleontologyTableBlockEntity paleontologyTableBlockEntity) {
                paleontologyTableBlockEntity.drops();
                level.updateNeighbourForOutputSignal(pos, this);
            }
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, @NotNull BlockState state, @NotNull BlockEntityType<T> blockEntityType) {
        return level.isClientSide() ? null : createTickerHelper(blockEntityType, ModBlockEntities.PALEONTOLOGY_TABLE.get(), PaleontologyTableBlockEntity::serverTick);
    }

    @Override
    protected @NotNull InteractionResult useWithoutItem(@NotNull BlockState state, Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull BlockHitResult hitResult) {
        if (!level.isClientSide()) {
            if (level.getBlockEntity(pos) instanceof PaleontologyTableBlockEntity blockEntity) {
                player.openMenu(new SimpleMenuProvider(
                        (containerId, playerInventory, player1) ->
                                new PaleontologyTableMenu(containerId, playerInventory, blockEntity, blockEntity.getDataAccess()),
                        Component.translatable("block.primordial_park.paleontology_table")
                ), pos);
            }
        }

        return InteractionResult.sidedSuccess(level.isClientSide());
    }

    @Override
    protected boolean hasAnalogOutputSignal(@NotNull BlockState state) {
        return true;
    }

    @Override
    protected int getAnalogOutputSignal(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos) {
        BlockEntity blockEntity = level.getBlockEntity(pos);

        if (blockEntity instanceof PaleontologyTableBlockEntity paleontologyTable) {
            ItemStack resultStack = paleontologyTable.inventory.getStackInSlot(PaleontologyTableBlockEntity.SLOT_RESULT);

            if (resultStack.isEmpty()) return 0;

            int maxStackSize =  resultStack.getMaxStackSize();
            int stackSize = resultStack.getCount();

            return (int) Math.floor((double) stackSize / (double) maxStackSize * 14.0) + 1;
        }

        return 0;
    }
}
