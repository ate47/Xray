package fr.atesab.xray.config;

import java.util.Objects;

import com.google.gson.annotations.Expose;

import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import fr.atesab.xray.SideRenderer;
import fr.atesab.xray.XrayMain;
import fr.atesab.xray.color.EnumElement;
import fr.atesab.xray.view.ViewMode;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class BlockConfig extends AbstractModeConfig implements SideRenderer, Cloneable {
    public enum Template implements EnumElement {
        // @formatter:off
        BLANK("x13.mod.template.blank", new ItemStack(Items.PAPER), new BlockConfig()),
        XRAY("x13.mod.template.xray", new ItemStack(Blocks.DIAMOND_ORE), new BlockConfig(
                GLFW.GLFW_KEY_X,
                0,
                "Xray",
                ViewMode.EXCLUSIVE,

                /* Ores */
                Blocks.COAL_ORE, Blocks.IRON_ORE, Blocks.GOLD_ORE, Blocks.DIAMOND_ORE,
                Blocks.EMERALD_ORE, Blocks.REDSTONE_ORE, Blocks.LAPIS_ORE, Blocks.NETHER_GOLD_ORE,
                Blocks.ANCIENT_DEBRIS, Blocks.NETHER_QUARTZ_ORE,

                // 1.17
                Blocks.COPPER_ORE, Blocks.DEEPSLATE_COAL_ORE, Blocks.DEEPSLATE_IRON_ORE, Blocks.DEEPSLATE_GOLD_ORE,
                Blocks.DEEPSLATE_DIAMOND_ORE, Blocks.DEEPSLATE_EMERALD_ORE, Blocks.DEEPSLATE_REDSTONE_ORE,
                Blocks.DEEPSLATE_LAPIS_ORE,

                Blocks.RAW_COPPER_BLOCK, Blocks.RAW_GOLD_BLOCK, Blocks.RAW_IRON_BLOCK, Blocks.CRYING_OBSIDIAN,

                // 1.18
                Blocks.COPPER_BLOCK,
                
                /* Ore Blocks */
                Blocks.COAL_BLOCK, Blocks.IRON_BLOCK, Blocks.GOLD_BLOCK, Blocks.DIAMOND_BLOCK,
                Blocks.EMERALD_BLOCK, Blocks.REDSTONE_BLOCK, Blocks.LAPIS_BLOCK, Blocks.NETHERITE_BLOCK,

                /* Blocks */
                Blocks.OBSIDIAN, Blocks.BLUE_ICE, Blocks.CLAY, Blocks.BOOKSHELF,
                Blocks.SPONGE, Blocks.WET_SPONGE,

                /* Other */
                Blocks.NETHER_WART, Blocks.SPAWNER, Blocks.LAVA, Blocks.WATER,
                Blocks.TNT, Blocks.CONDUIT,

                /* Portals */
                Blocks.END_PORTAL_FRAME, Blocks.END_PORTAL, Blocks.NETHER_PORTAL,

                /* Interactive */
                Blocks.BEACON, Blocks.CHEST, Blocks.TRAPPED_CHEST, Blocks.ENDER_CHEST,
                Blocks.DISPENSER, Blocks.DROPPER,

                /* Useless */
                Blocks.DRAGON_WALL_HEAD, Blocks.DRAGON_HEAD, Blocks.DRAGON_EGG,

                /* Infested (Silverfish inside) */
                Blocks.INFESTED_STONE, Blocks.INFESTED_STONE_BRICKS, Blocks.INFESTED_CRACKED_STONE_BRICKS,
                Blocks.INFESTED_COBBLESTONE, Blocks.INFESTED_CHISELED_STONE_BRICKS,
                Blocks.INFESTED_MOSSY_STONE_BRICKS)),
        CAVE("x13.mod.template.cave", new ItemStack(Blocks.STONE), new BlockConfig(
                GLFW.GLFW_KEY_C,
                0,
                "Cave",
                ViewMode.INCLUSIVE,
                Blocks.DIRT,              Blocks.GRASS,            Blocks.GRAVEL,          Blocks.GRASS_BLOCK,
                Blocks.DIRT_PATH,         Blocks.SAND,             Blocks.SANDSTONE,       Blocks.RED_SAND
            )),
        REDSTONE("x13.mod.template.redstone", new ItemStack(Blocks.REDSTONE_ORE), new BlockConfig(
                GLFW.GLFW_KEY_R,
                0,
                "Redstone",
                ViewMode.EXCLUSIVE,

                Blocks.REDSTONE_BLOCK,                             Blocks.REDSTONE_LAMP,
                Blocks.REDSTONE_ORE,                               Blocks.REDSTONE_TORCH, 
                Blocks.DEEPSLATE_REDSTONE_ORE,
                Blocks.REDSTONE_WALL_TORCH,                        Blocks.REDSTONE_WIRE,
                Blocks.REPEATER,                                   Blocks.REPEATING_COMMAND_BLOCK,
                Blocks.COMMAND_BLOCK,                              Blocks.CHAIN_COMMAND_BLOCK,
                Blocks.COMPARATOR,                                 Blocks.ANVIL,
                Blocks.CHEST,                                      Blocks.TRAPPED_CHEST,
                Blocks.DROPPER,                                    Blocks.DISPENSER,
                Blocks.HOPPER,                                     Blocks.OBSERVER,
                Blocks.DRAGON_HEAD,                                Blocks.DRAGON_WALL_HEAD,
                Blocks.IRON_DOOR,                                  Blocks.ACACIA_DOOR,
                Blocks.BIRCH_DOOR,                                 Blocks.DARK_OAK_DOOR,
                Blocks.JUNGLE_DOOR,                                Blocks.OAK_DOOR,
                Blocks.SPRUCE_DOOR,                                Blocks.ACACIA_BUTTON,
                Blocks.BIRCH_BUTTON,                               Blocks.DARK_OAK_BUTTON,
                Blocks.JUNGLE_BUTTON,                              Blocks.OAK_BUTTON,
                Blocks.SPRUCE_BUTTON,                              Blocks.STONE_BUTTON,
                Blocks.LEVER,                                      Blocks.TNT,
                Blocks.PISTON,                                     Blocks.PISTON_HEAD,
                Blocks.MOVING_PISTON,                              Blocks.STICKY_PISTON,
                Blocks.NOTE_BLOCK,                                 Blocks.DAYLIGHT_DETECTOR,
                Blocks.IRON_TRAPDOOR,                              Blocks.ACACIA_TRAPDOOR,
                Blocks.BIRCH_TRAPDOOR,                             Blocks.DARK_OAK_TRAPDOOR,
                Blocks.JUNGLE_TRAPDOOR,                            Blocks.OAK_TRAPDOOR,
                Blocks.SPRUCE_TRAPDOOR,                            Blocks.ACACIA_PRESSURE_PLATE,
                Blocks.BIRCH_PRESSURE_PLATE,                       Blocks.DARK_OAK_PRESSURE_PLATE,
                Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE,              Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE,
                Blocks.JUNGLE_PRESSURE_PLATE,                      Blocks.OAK_PRESSURE_PLATE,
                Blocks.SPRUCE_PRESSURE_PLATE,                      Blocks.STONE_PRESSURE_PLATE,
                Blocks.RAIL,                                       Blocks.ACTIVATOR_RAIL,
                Blocks.DETECTOR_RAIL,                              Blocks.POWERED_RAIL,
                Blocks.ENDER_CHEST,								   Blocks.TARGET
            ));
        // @formatter:on

        private Component title;
        private ItemStack icon;
        private BlockConfig cfg;

        Template(String translation, ItemStack icon, BlockConfig cfg) {
            this.title = new TranslatableComponent(translation);
            this.icon = icon;
            this.cfg = cfg;
        }

        @Override
        public Component getTitle() {
            return title;
        }

        @Override
        public ItemStack getIcon() {
            return icon;
        }

        public BlockConfig create() {
            return cfg.clone();
        }

        public BlockConfig create(int color) {
            BlockConfig cfg = create();
            cfg.setColor(color);
            return cfg;
        }

        public void cloneInto(BlockConfig cfg) {
            cfg.cloneInto(this.cfg);
        }

    }

    @Expose
    private SyncedBlockList blocks;

    @Expose
    private ViewMode viewMode;

    public BlockConfig() {
        this(ViewMode.EXCLUSIVE);
    }

    private BlockConfig(BlockConfig other) {
        super(other);
    }

    public BlockConfig(ViewMode viewMode, Block... blocks) {
        super();
        this.viewMode = Objects.requireNonNull(viewMode, "viewMode can't be null!");
        this.blocks = new SyncedBlockList(blocks);
    }

    public BlockConfig(int key, int ScanCode, String name, ViewMode viewMode, Block... blocks) {
        super(key, ScanCode, name);
        this.viewMode = Objects.requireNonNull(viewMode, "viewMode can't be null!");
        this.blocks = new SyncedBlockList(blocks);
    }

    @Override
    public void cloneInto(AbstractModeConfig cfg) {
        if (!(cfg instanceof BlockConfig other))
            throw new IllegalArgumentException("Can't clone config from another type!");

        super.cloneInto(cfg);

        this.viewMode = other.viewMode;
        this.blocks = other.blocks.clone();
    }

    public SyncedBlockList getBlocks() {
        return blocks;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void shouldSideBeRendered(BlockState adjacentState, BlockGetter blockState, BlockPos blockAccess,
            Direction pos, CallbackInfoReturnable<Boolean> ci) {
        if (!isEnabled())
            return;

        String name = Registry.BLOCK.getKey(adjacentState.getBlock()).toString();
        boolean present = blocks.contains(name);
        boolean shouldRender = viewMode.getViewer().shouldRenderSide(present, adjacentState, blockState,
                blockAccess, pos);
        ci.setReturnValue(shouldRender);
    }

    @Override
    public void setEnabled(boolean enabled) {
        setEnabled(enabled, true);
    }

    public void setEnabled(boolean enable, boolean reloadRenderers) {
        XrayMain mod = XrayMain.getMod();

        if (enable) {
            // disable the previous mode
            BlockConfig old = mod.getConfig().getSelectedBlockMode();
            if (old != null)
                old.setEnabled(false);
        }

        super.setEnabled(enable);

        mod.internalFullbright();

        if (reloadRenderers)
            Minecraft.getInstance().levelRenderer.allChanged();
    }

    public ViewMode getViewMode() {
        return viewMode;
    }

    public void setViewMode(ViewMode viewMode) {
        this.viewMode = viewMode;
    }

    @Override
    public BlockConfig clone() {
        return new BlockConfig(this);
    }

}
