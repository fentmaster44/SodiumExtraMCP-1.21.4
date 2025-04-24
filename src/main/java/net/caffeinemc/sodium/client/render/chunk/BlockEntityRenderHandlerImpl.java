package net.caffeinemc.sodium.client.render.chunk;

import net.caffeinemc.sodium.api.blockentity.BlockEntityRenderHandler;
import net.caffeinemc.sodium.api.blockentity.BlockEntityRenderPredicate;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class BlockEntityRenderHandlerImpl implements BlockEntityRenderHandler {
    @Override
    public <T extends BlockEntity> void addRenderPredicate(BlockEntityType<T> type, BlockEntityRenderPredicate predicate) {
        ExtendedBlockEntityType.addRenderPredicate(type, predicate);
    }

    @Override
    public <T extends BlockEntity> boolean removeRenderPredicate(BlockEntityType<T> type, BlockEntityRenderPredicate predicate) {
        return ExtendedBlockEntityType.removeRenderPredicate(type, predicate);
    }
}
