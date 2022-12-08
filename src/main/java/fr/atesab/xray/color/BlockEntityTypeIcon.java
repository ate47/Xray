package fr.atesab.xray.color;

import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

public record BlockEntityTypeIcon(BlockEntityType<?> entity, ItemStack icon) {

    private static final Map<String, ItemStack> ICONS = new HashMap<>();
    private static final ItemStack DEFAULT_ICON = new ItemStack(Items.PAPER);

    public static final BlockEntityTypeIcon FURNACE = register(BlockEntityType.FURNACE, Items.FURNACE);
    public static final BlockEntityTypeIcon CHEST = register(BlockEntityType.CHEST, Items.CHEST);
    public static final BlockEntityTypeIcon TRAPPED_CHEST = register(BlockEntityType.TRAPPED_CHEST, Items.TRAPPED_CHEST);
    public static final BlockEntityTypeIcon ENDER_CHEST = register(BlockEntityType.ENDER_CHEST, Items.ENDER_CHEST);
    public static final BlockEntityTypeIcon JUKEBOX = register(BlockEntityType.JUKEBOX, Items.JUKEBOX);
    public static final BlockEntityTypeIcon DISPENSER = register(BlockEntityType.DISPENSER, Items.DISPENSER);
    public static final BlockEntityTypeIcon DROPPER = register(BlockEntityType.DROPPER, Items.DROPPER);
    public static final BlockEntityTypeIcon SIGN = register(BlockEntityType.SIGN, Items.SPRUCE_SIGN);
    public static final BlockEntityTypeIcon MOB_SPAWNER = register(BlockEntityType.MOB_SPAWNER, Items.SPAWNER);
    public static final BlockEntityTypeIcon PISTON = register(BlockEntityType.PISTON, Items.PISTON);
    public static final BlockEntityTypeIcon BREWING_STAND = register(BlockEntityType.BREWING_STAND, Items.BREWING_STAND);
    public static final BlockEntityTypeIcon ENCHANTING_TABLE = register(BlockEntityType.ENCHANTING_TABLE, Items.ENCHANTING_TABLE);
    public static final BlockEntityTypeIcon END_PORTAL = register(BlockEntityType.END_PORTAL, Items.END_PORTAL_FRAME);
    public static final BlockEntityTypeIcon BEACON = register(BlockEntityType.BEACON, Items.BEACON);
    public static final BlockEntityTypeIcon SKULL = register(BlockEntityType.SKULL, Items.WITHER_SKELETON_SKULL);
    public static final BlockEntityTypeIcon DAYLIGHT_DETECTOR = register(BlockEntityType.DAYLIGHT_DETECTOR, Items.DAYLIGHT_DETECTOR);
    public static final BlockEntityTypeIcon HOPPER = register(BlockEntityType.HOPPER, Items.HOPPER);
    public static final BlockEntityTypeIcon COMPARATOR = register(BlockEntityType.COMPARATOR, Items.COMPARATOR);
    public static final BlockEntityTypeIcon BANNER = register(BlockEntityType.BANNER, Items.WHITE_BANNER);
    public static final BlockEntityTypeIcon STRUCTURE_BLOCK = register(BlockEntityType.STRUCTURE_BLOCK, Items.STRUCTURE_BLOCK);
    public static final BlockEntityTypeIcon END_GATEWAY = register(BlockEntityType.END_GATEWAY, Items.END_PORTAL_FRAME);
    public static final BlockEntityTypeIcon COMMAND_BLOCK = register(BlockEntityType.COMMAND_BLOCK, Items.COMMAND_BLOCK);
    public static final BlockEntityTypeIcon SHULKER_BOX = register(BlockEntityType.SHULKER_BOX, Items.SHULKER_BOX);
    public static final BlockEntityTypeIcon BED = register(BlockEntityType.BED, Items.RED_BED);
    public static final BlockEntityTypeIcon CONDUIT = register(BlockEntityType.CONDUIT, Items.CONDUIT);
    public static final BlockEntityTypeIcon BARREL = register(BlockEntityType.BARREL, Items.BARREL);
    public static final BlockEntityTypeIcon SMOKER = register(BlockEntityType.SMOKER, Items.SMOKER);
    public static final BlockEntityTypeIcon BLAST_FURNACE = register(BlockEntityType.BLAST_FURNACE, Items.BLAST_FURNACE);
    public static final BlockEntityTypeIcon LECTERN = register(BlockEntityType.LECTERN, Items.LECTERN);
    public static final BlockEntityTypeIcon BELL = register(BlockEntityType.BELL, Items.BELL);
    public static final BlockEntityTypeIcon JIGSAW = register(BlockEntityType.JIGSAW, Items.JIGSAW);
    public static final BlockEntityTypeIcon CAMPFIRE = register(BlockEntityType.CAMPFIRE, Items.CAMPFIRE);
    public static final BlockEntityTypeIcon BEEHIVE = register(BlockEntityType.BEEHIVE, Items.BEEHIVE);
    public static final BlockEntityTypeIcon SCULK_SENSOR = register(BlockEntityType.SCULK_SENSOR, Items.SCULK_SENSOR);
    public static final BlockEntityTypeIcon SCULK_CATALYST = register(BlockEntityType.SCULK_CATALYST, Items.SCULK_CATALYST);
    public static final BlockEntityTypeIcon SCULK_SHRIEKER = register(BlockEntityType.SCULK_SHRIEKER, Items.SCULK_SHRIEKER);

    public static BlockEntityTypeIcon register(BlockEntityType<?> type, ItemConvertible icon) {
        return register(type, new ItemStack(icon));
    }

    public static BlockEntityTypeIcon register(BlockEntityType<?> type, ItemStack icon) {
        Identifier id = Registries.BLOCK_ENTITY_TYPE.getId(type);
        if (id != null) {
            ICONS.put(id.toTranslationKey(), icon);
        }

        return new BlockEntityTypeIcon(type, icon);
    }

    @SuppressWarnings("deprecation")
    public static ItemStack getIcon(BlockEntityType<?> type) {
        Identifier id = Registries.BLOCK_ENTITY_TYPE.getId(type);
        if (id == null) {
            return DEFAULT_ICON;
        }

        ItemStack icon = ICONS.get(id.toTranslationKey());

        if (icon != null) {
            return icon;
        }

        return DEFAULT_ICON;
    }
}
