package net.caffeinemc.sodium.client.render.chunk;

import lombok.val;
import net.caffeinemc.sodium.api.blockentity.BlockEntityRenderPredicate;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

@SuppressWarnings("unchecked")
public interface ExtendedBlockEntityType {

    static <T extends BlockEntity> boolean shouldRender(BlockEntityType<? extends T> type,
                                                        BlockGetter blockGetter,
                                                        BlockPos blockPos,
                                                        BlockEntity entity) {
       val predicates = type.getRenderPredicates();

        for (int i = 0; i < predicates.length; i++) {
            if (!predicates[i].shouldRender(blockGetter, blockPos, entity)) {
                return false;
            }
        }

        return true;
    }

    static <T extends BlockEntity> void addRenderPredicate(BlockEntityType<T> type,
                                                           BlockEntityRenderPredicate predicate) {
        type.addRenderPredicate(predicate);
    }

    static <T extends BlockEntity> boolean removeRenderPredicate(BlockEntityType<T> type,
                                                                 BlockEntityRenderPredicate predicate) {
        return type.removeRenderPredicate(predicate);
    }
}
