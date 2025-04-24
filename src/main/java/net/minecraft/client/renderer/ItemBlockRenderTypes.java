package net.minecraft.client.renderer;

import java.util.Map;

import it.unimi.dsi.fastutil.objects.Reference2ReferenceOpenHashMap;
import lombok.val;
import net.caffeinemc.sodium.client.SodiumClientMod;
import net.minecraft.client.GraphicsStatus;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ItemBlockRenderTypes {
    // Sodium
    private static final Map<Block, RenderType> TYPE_BY_BLOCK = new Reference2ReferenceOpenHashMap<>();
    private static final Map<Fluid, RenderType> TYPE_BY_FLUID = new Reference2ReferenceOpenHashMap<>();

    // Sodium
    static {
        val rendertype = RenderType.translucent();
        TYPE_BY_FLUID.put(Fluids.FLOWING_WATER, rendertype);
        TYPE_BY_FLUID.put(Fluids.WATER, rendertype);

        val rendertype_ = RenderType.tripwire();
        TYPE_BY_BLOCK.put(Blocks.TRIPWIRE, rendertype_);

        val rendertype1 = RenderType.cutoutMipped();
        TYPE_BY_BLOCK.put(Blocks.GRASS_BLOCK, rendertype1);
        TYPE_BY_BLOCK.put(Blocks.IRON_BARS, rendertype1);
        TYPE_BY_BLOCK.put(Blocks.GLASS_PANE, rendertype1);
        TYPE_BY_BLOCK.put(Blocks.TRIPWIRE_HOOK, rendertype1);
        TYPE_BY_BLOCK.put(Blocks.HOPPER, rendertype1);
        TYPE_BY_BLOCK.put(Blocks.CHAIN, rendertype1);
        TYPE_BY_BLOCK.put(Blocks.JUNGLE_LEAVES, rendertype1);
        TYPE_BY_BLOCK.put(Blocks.OAK_LEAVES, rendertype1);
        TYPE_BY_BLOCK.put(Blocks.SPRUCE_LEAVES, rendertype1);
        TYPE_BY_BLOCK.put(Blocks.ACACIA_LEAVES, rendertype1);
        TYPE_BY_BLOCK.put(Blocks.CHERRY_LEAVES, rendertype1);
        TYPE_BY_BLOCK.put(Blocks.BIRCH_LEAVES, rendertype1);
        TYPE_BY_BLOCK.put(Blocks.DARK_OAK_LEAVES, rendertype1);
        TYPE_BY_BLOCK.put(Blocks.PALE_OAK_LEAVES, rendertype1);
        TYPE_BY_BLOCK.put(Blocks.AZALEA_LEAVES, rendertype1);
        TYPE_BY_BLOCK.put(Blocks.FLOWERING_AZALEA_LEAVES, rendertype1);
        TYPE_BY_BLOCK.put(Blocks.MANGROVE_ROOTS, rendertype1);
        TYPE_BY_BLOCK.put(Blocks.MANGROVE_LEAVES, rendertype1);

        val rendertype2 = RenderType.cutout();
        TYPE_BY_BLOCK.put(Blocks.OAK_SAPLING, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.SPRUCE_SAPLING, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.BIRCH_SAPLING, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.JUNGLE_SAPLING, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.ACACIA_SAPLING, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.CHERRY_SAPLING, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.DARK_OAK_SAPLING, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.PALE_OAK_SAPLING, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.GLASS, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.WHITE_BED, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.ORANGE_BED, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.MAGENTA_BED, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.LIGHT_BLUE_BED, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.YELLOW_BED, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.LIME_BED, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.PINK_BED, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.GRAY_BED, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.LIGHT_GRAY_BED, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.CYAN_BED, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.PURPLE_BED, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.BLUE_BED, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.BROWN_BED, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.GREEN_BED, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.RED_BED, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.BLACK_BED, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.POWERED_RAIL, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.DETECTOR_RAIL, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.COBWEB, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.SHORT_GRASS, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.FERN, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.DEAD_BUSH, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.SEAGRASS, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.TALL_SEAGRASS, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.DANDELION, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.OPEN_EYEBLOSSOM, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.CLOSED_EYEBLOSSOM, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.POPPY, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.BLUE_ORCHID, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.ALLIUM, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.AZURE_BLUET, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.RED_TULIP, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.ORANGE_TULIP, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.WHITE_TULIP, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.PINK_TULIP, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.OXEYE_DAISY, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.CORNFLOWER, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.WITHER_ROSE, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.LILY_OF_THE_VALLEY, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.BROWN_MUSHROOM, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.RED_MUSHROOM, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.TORCH, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.WALL_TORCH, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.SOUL_TORCH, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.SOUL_WALL_TORCH, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.FIRE, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.SOUL_FIRE, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.SPAWNER, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.TRIAL_SPAWNER, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.VAULT, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.REDSTONE_WIRE, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.WHEAT, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.OAK_DOOR, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.LADDER, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.RAIL, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.IRON_DOOR, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.REDSTONE_TORCH, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.REDSTONE_WALL_TORCH, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.CACTUS, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.SUGAR_CANE, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.REPEATER, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.OAK_TRAPDOOR, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.SPRUCE_TRAPDOOR, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.BIRCH_TRAPDOOR, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.JUNGLE_TRAPDOOR, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.ACACIA_TRAPDOOR, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.CHERRY_TRAPDOOR, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.DARK_OAK_TRAPDOOR, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.PALE_OAK_TRAPDOOR, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.CRIMSON_TRAPDOOR, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.WARPED_TRAPDOOR, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.MANGROVE_TRAPDOOR, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.BAMBOO_TRAPDOOR, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.COPPER_TRAPDOOR, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.EXPOSED_COPPER_TRAPDOOR, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.WEATHERED_COPPER_TRAPDOOR, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.OXIDIZED_COPPER_TRAPDOOR, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.WAXED_COPPER_TRAPDOOR, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.WAXED_EXPOSED_COPPER_TRAPDOOR, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.WAXED_WEATHERED_COPPER_TRAPDOOR, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.WAXED_OXIDIZED_COPPER_TRAPDOOR, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.ATTACHED_PUMPKIN_STEM, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.ATTACHED_MELON_STEM, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.PUMPKIN_STEM, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.MELON_STEM, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.VINE, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.PALE_MOSS_CARPET, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.PALE_HANGING_MOSS, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.GLOW_LICHEN, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.RESIN_CLUMP, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.LILY_PAD, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.NETHER_WART, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.BREWING_STAND, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.COCOA, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.BEACON, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.FLOWER_POT, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.POTTED_OAK_SAPLING, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.POTTED_SPRUCE_SAPLING, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.POTTED_BIRCH_SAPLING, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.POTTED_JUNGLE_SAPLING, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.POTTED_ACACIA_SAPLING, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.POTTED_CHERRY_SAPLING, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.POTTED_DARK_OAK_SAPLING, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.POTTED_PALE_OAK_SAPLING, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.POTTED_MANGROVE_PROPAGULE, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.POTTED_FERN, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.POTTED_DANDELION, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.POTTED_POPPY, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.POTTED_OPEN_EYEBLOSSOM, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.POTTED_CLOSED_EYEBLOSSOM, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.POTTED_BLUE_ORCHID, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.POTTED_ALLIUM, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.POTTED_AZURE_BLUET, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.POTTED_RED_TULIP, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.POTTED_ORANGE_TULIP, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.POTTED_WHITE_TULIP, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.POTTED_PINK_TULIP, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.POTTED_OXEYE_DAISY, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.POTTED_CORNFLOWER, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.POTTED_LILY_OF_THE_VALLEY, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.POTTED_WITHER_ROSE, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.POTTED_RED_MUSHROOM, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.POTTED_BROWN_MUSHROOM, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.POTTED_DEAD_BUSH, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.POTTED_CACTUS, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.POTTED_AZALEA, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.POTTED_FLOWERING_AZALEA, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.POTTED_TORCHFLOWER, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.CARROTS, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.POTATOES, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.COMPARATOR, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.ACTIVATOR_RAIL, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.IRON_TRAPDOOR, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.SUNFLOWER, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.LILAC, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.ROSE_BUSH, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.PEONY, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.TALL_GRASS, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.LARGE_FERN, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.SPRUCE_DOOR, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.BIRCH_DOOR, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.JUNGLE_DOOR, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.ACACIA_DOOR, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.CHERRY_DOOR, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.DARK_OAK_DOOR, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.PALE_OAK_DOOR, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.MANGROVE_DOOR, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.BAMBOO_DOOR, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.COPPER_DOOR, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.EXPOSED_COPPER_DOOR, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.WEATHERED_COPPER_DOOR, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.OXIDIZED_COPPER_DOOR, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.WAXED_COPPER_DOOR, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.WAXED_EXPOSED_COPPER_DOOR, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.WAXED_WEATHERED_COPPER_DOOR, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.WAXED_OXIDIZED_COPPER_DOOR, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.END_ROD, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.CHORUS_PLANT, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.CHORUS_FLOWER, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.TORCHFLOWER, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.TORCHFLOWER_CROP, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.PITCHER_PLANT, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.PITCHER_CROP, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.BEETROOTS, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.KELP, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.KELP_PLANT, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.TURTLE_EGG, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.DEAD_TUBE_CORAL, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.DEAD_BRAIN_CORAL, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.DEAD_BUBBLE_CORAL, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.DEAD_FIRE_CORAL, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.DEAD_HORN_CORAL, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.TUBE_CORAL, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.BRAIN_CORAL, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.BUBBLE_CORAL, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.FIRE_CORAL, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.HORN_CORAL, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.DEAD_TUBE_CORAL_FAN, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.DEAD_BRAIN_CORAL_FAN, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.DEAD_BUBBLE_CORAL_FAN, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.DEAD_FIRE_CORAL_FAN, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.DEAD_HORN_CORAL_FAN, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.TUBE_CORAL_FAN, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.BRAIN_CORAL_FAN, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.BUBBLE_CORAL_FAN, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.FIRE_CORAL_FAN, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.HORN_CORAL_FAN, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.DEAD_TUBE_CORAL_WALL_FAN, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.DEAD_BRAIN_CORAL_WALL_FAN, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.DEAD_BUBBLE_CORAL_WALL_FAN, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.DEAD_FIRE_CORAL_WALL_FAN, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.DEAD_HORN_CORAL_WALL_FAN, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.TUBE_CORAL_WALL_FAN, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.BRAIN_CORAL_WALL_FAN, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.BUBBLE_CORAL_WALL_FAN, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.FIRE_CORAL_WALL_FAN, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.HORN_CORAL_WALL_FAN, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.SEA_PICKLE, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.CONDUIT, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.BAMBOO_SAPLING, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.BAMBOO, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.POTTED_BAMBOO, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.SCAFFOLDING, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.STONECUTTER, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.LANTERN, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.SOUL_LANTERN, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.CAMPFIRE, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.SOUL_CAMPFIRE, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.SWEET_BERRY_BUSH, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.WEEPING_VINES, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.WEEPING_VINES_PLANT, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.TWISTING_VINES, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.TWISTING_VINES_PLANT, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.NETHER_SPROUTS, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.CRIMSON_FUNGUS, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.WARPED_FUNGUS, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.CRIMSON_ROOTS, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.WARPED_ROOTS, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.POTTED_CRIMSON_FUNGUS, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.POTTED_WARPED_FUNGUS, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.POTTED_CRIMSON_ROOTS, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.POTTED_WARPED_ROOTS, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.CRIMSON_DOOR, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.WARPED_DOOR, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.POINTED_DRIPSTONE, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.SMALL_AMETHYST_BUD, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.MEDIUM_AMETHYST_BUD, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.LARGE_AMETHYST_BUD, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.AMETHYST_CLUSTER, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.LIGHTNING_ROD, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.CAVE_VINES, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.CAVE_VINES_PLANT, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.SPORE_BLOSSOM, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.FLOWERING_AZALEA, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.AZALEA, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.PINK_PETALS, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.BIG_DRIPLEAF, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.BIG_DRIPLEAF_STEM, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.SMALL_DRIPLEAF, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.HANGING_ROOTS, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.SCULK_SENSOR, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.CALIBRATED_SCULK_SENSOR, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.SCULK_VEIN, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.SCULK_SHRIEKER, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.MANGROVE_PROPAGULE, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.FROGSPAWN, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.COPPER_GRATE, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.EXPOSED_COPPER_GRATE, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.WEATHERED_COPPER_GRATE, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.OXIDIZED_COPPER_GRATE, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.WAXED_COPPER_GRATE, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.WAXED_EXPOSED_COPPER_GRATE, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.WAXED_WEATHERED_COPPER_GRATE, rendertype2);
        TYPE_BY_BLOCK.put(Blocks.WAXED_OXIDIZED_COPPER_GRATE, rendertype2);

        val rendertype3 = RenderType.translucent();
        TYPE_BY_BLOCK.put(Blocks.ICE, rendertype3);
        TYPE_BY_BLOCK.put(Blocks.NETHER_PORTAL, rendertype3);
        TYPE_BY_BLOCK.put(Blocks.WHITE_STAINED_GLASS, rendertype3);
        TYPE_BY_BLOCK.put(Blocks.ORANGE_STAINED_GLASS, rendertype3);
        TYPE_BY_BLOCK.put(Blocks.MAGENTA_STAINED_GLASS, rendertype3);
        TYPE_BY_BLOCK.put(Blocks.LIGHT_BLUE_STAINED_GLASS, rendertype3);
        TYPE_BY_BLOCK.put(Blocks.YELLOW_STAINED_GLASS, rendertype3);
        TYPE_BY_BLOCK.put(Blocks.LIME_STAINED_GLASS, rendertype3);
        TYPE_BY_BLOCK.put(Blocks.PINK_STAINED_GLASS, rendertype3);
        TYPE_BY_BLOCK.put(Blocks.GRAY_STAINED_GLASS, rendertype3);
        TYPE_BY_BLOCK.put(Blocks.LIGHT_GRAY_STAINED_GLASS, rendertype3);
        TYPE_BY_BLOCK.put(Blocks.CYAN_STAINED_GLASS, rendertype3);
        TYPE_BY_BLOCK.put(Blocks.PURPLE_STAINED_GLASS, rendertype3);
        TYPE_BY_BLOCK.put(Blocks.BLUE_STAINED_GLASS, rendertype3);
        TYPE_BY_BLOCK.put(Blocks.BROWN_STAINED_GLASS, rendertype3);
        TYPE_BY_BLOCK.put(Blocks.GREEN_STAINED_GLASS, rendertype3);
        TYPE_BY_BLOCK.put(Blocks.RED_STAINED_GLASS, rendertype3);
        TYPE_BY_BLOCK.put(Blocks.BLACK_STAINED_GLASS, rendertype3);
        TYPE_BY_BLOCK.put(Blocks.WHITE_STAINED_GLASS_PANE, rendertype3);
        TYPE_BY_BLOCK.put(Blocks.ORANGE_STAINED_GLASS_PANE, rendertype3);
        TYPE_BY_BLOCK.put(Blocks.MAGENTA_STAINED_GLASS_PANE, rendertype3);
        TYPE_BY_BLOCK.put(Blocks.LIGHT_BLUE_STAINED_GLASS_PANE, rendertype3);
        TYPE_BY_BLOCK.put(Blocks.YELLOW_STAINED_GLASS_PANE, rendertype3);
        TYPE_BY_BLOCK.put(Blocks.LIME_STAINED_GLASS_PANE, rendertype3);
        TYPE_BY_BLOCK.put(Blocks.PINK_STAINED_GLASS_PANE, rendertype3);
        TYPE_BY_BLOCK.put(Blocks.GRAY_STAINED_GLASS_PANE, rendertype3);
        TYPE_BY_BLOCK.put(Blocks.LIGHT_GRAY_STAINED_GLASS_PANE, rendertype3);
        TYPE_BY_BLOCK.put(Blocks.CYAN_STAINED_GLASS_PANE, rendertype3);
        TYPE_BY_BLOCK.put(Blocks.PURPLE_STAINED_GLASS_PANE, rendertype3);
        TYPE_BY_BLOCK.put(Blocks.BLUE_STAINED_GLASS_PANE, rendertype3);
        TYPE_BY_BLOCK.put(Blocks.BROWN_STAINED_GLASS_PANE, rendertype3);
        TYPE_BY_BLOCK.put(Blocks.GREEN_STAINED_GLASS_PANE, rendertype3);
        TYPE_BY_BLOCK.put(Blocks.RED_STAINED_GLASS_PANE, rendertype3);
        TYPE_BY_BLOCK.put(Blocks.BLACK_STAINED_GLASS_PANE, rendertype3);
        TYPE_BY_BLOCK.put(Blocks.SLIME_BLOCK, rendertype3);
        TYPE_BY_BLOCK.put(Blocks.HONEY_BLOCK, rendertype3);
        TYPE_BY_BLOCK.put(Blocks.FROSTED_ICE, rendertype3);
        TYPE_BY_BLOCK.put(Blocks.BUBBLE_COLUMN, rendertype3);
        TYPE_BY_BLOCK.put(Blocks.TINTED_GLASS, rendertype3);
    }

    private static boolean renderCutout;

    // Sodium
    private static boolean leavesFancy;

    public static RenderType getChunkRenderType(BlockState p_109283_) {
        Block block = p_109283_.getBlock();
        if (block instanceof LeavesBlock) {
            return leavesFancy ? RenderType.cutoutMipped() : RenderType.solid();
        } else {
            RenderType rendertype = TYPE_BY_BLOCK.get(block);
            return rendertype != null ? rendertype : RenderType.solid();
        }
    }

    public static RenderType getMovingBlockRenderType(BlockState p_109294_) {
        Block block = p_109294_.getBlock();
        if (block instanceof LeavesBlock) {
            return leavesFancy ? RenderType.cutoutMipped() : RenderType.solid();
        } else {
            RenderType rendertype = TYPE_BY_BLOCK.get(block);
            if (rendertype != null) {
                return rendertype == RenderType.translucent() ? RenderType.translucentMovingBlock() : rendertype;
            } else {
                return RenderType.solid();
            }
        }
    }

    public static RenderType getRenderType(BlockState p_364446_) {
        RenderType rendertype = getChunkRenderType(p_364446_);
        return rendertype == RenderType.translucent() ? Sheets.translucentItemSheet() : Sheets.cutoutBlockSheet();
    }

    public static RenderType getRenderType(ItemStack p_363859_) {
        if (p_363859_.getItem() instanceof BlockItem blockitem) {
            Block block = blockitem.getBlock();
            return getRenderType(block.defaultBlockState());
        } else {
            return Sheets.translucentItemSheet();
        }
    }

    public static RenderType getRenderLayer(FluidState p_109288_) {
        RenderType rendertype = TYPE_BY_FLUID.get(p_109288_.getType());
        return rendertype != null ? rendertype : RenderType.solid();
    }

    // Sodium
    public static void setFancy(boolean fancyGraphicsOrBetter) {
        leavesFancy = SodiumClientMod.options().quality.leavesQuality.isFancy(fancyGraphicsOrBetter ? GraphicsStatus.FANCY : GraphicsStatus.FAST);
    }
}