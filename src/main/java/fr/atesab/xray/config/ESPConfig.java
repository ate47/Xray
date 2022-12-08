package fr.atesab.xray.config;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

import com.google.gson.annotations.Expose;

import fr.atesab.xray.color.EntityTypeIcon;
import fr.atesab.xray.color.EnumElement;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;

import net.minecraft.util.Identifier;

public class ESPConfig extends AbstractModeConfig implements Cloneable {
    public enum Template implements EnumElement {
        BLANK("x13.mod.template.blank", new ItemStack(Items.PAPER), new ESPConfig()),
        PLAYER("x13.mod.esp.template.player", new ItemStack(Items.PLAYER_HEAD), 
            new ESPConfig(0, 0, "Player", EntityType.PLAYER)
        ),
        WITHER("x13.mod.esp.template.wither", new ItemStack(Items.WITHER_SKELETON_SKULL), 
            new ESPConfig(0, 0, "Wither", EntityType.WITHER, EntityType.WITHER_SKELETON)
        ),
        AGGRESIVE("x13.mod.esp.template.aggresive", new ItemStack(Items.CREEPER_HEAD), () -> 
             new ESPConfig(EntityTypeIcon.getEntityOfType(SpawnGroup.MONSTER), Collections.emptyList())
        ),
        PASSIVE("x13.mod.esp.template.passive", new ItemStack(Items.POPPY), () ->
                new ESPConfig(EntityTypeIcon.getEntityOfType(SpawnGroup.CREATURE), Collections.emptyList())
        ),
        CHEST("x13.mod.esp.template.chest", new ItemStack(Items.CHEST), () ->
                new ESPConfig(BlockEntityType.CHEST, BlockEntityType.ENDER_CHEST, BlockEntityType.HOPPER, BlockEntityType.TRAPPED_CHEST, BlockEntityType.MOB_SPAWNER)
        );

        private final Text title;
        private final ItemStack icon;
        private final Supplier<ESPConfig> cfg;

        Template(String translation, ItemStack icon, ESPConfig cfg) {
            this(translation, icon, () -> cfg);
        }

        Template(String translation, ItemStack icon, Supplier<ESPConfig> cfg) {
            this.title = Text.translatable(translation);
            this.icon = icon;
            this.cfg = cfg;
        }

        @Override
        public Text getTitle() {
            return title;
        }

        @Override
        public ItemStack getIcon() {
            return icon;
        }

        public ESPConfig create() {
            return cfg.get().clone();
        }

        public ESPConfig create(int color) {
            ESPConfig cfg = create();
            cfg.setColor(color);
            return cfg;
        }

        public void cloneInto(ESPConfig cfg) {
            cfg.cloneInto(this.cfg.get());
        }

    }

    @Expose
    private SyncedEntityTypeList entities;
    @Expose
    private SyncedBlockEntityTypeList blockEntities;
    @Expose
    private boolean tracer = false;

    public ESPConfig() {
        this(Collections.emptyList(), Collections.emptyList());
    }

    private ESPConfig(ESPConfig other) {
        super(other);
    }

    public ESPConfig(EntityType<?>... entities) {
        super();
        this.entities = new SyncedEntityTypeList(entities);
        this.blockEntities = new SyncedBlockEntityTypeList();
    }
    public ESPConfig(BlockEntityType<?>... entities) {
        super();
        this.entities = new SyncedEntityTypeList();
        this.blockEntities = new SyncedBlockEntityTypeList(entities);
    }

    public ESPConfig(List<EntityType<?>> entities, List<BlockEntityType<?>> blockEntities) {
        super();
        this.entities = new SyncedEntityTypeList(entities);
        this.blockEntities = new SyncedBlockEntityTypeList(blockEntities);
    }

    public ESPConfig(int key, int ScanCode, String name, EntityType<?>... entities) {
        super(key, ScanCode, name);
        this.entities = new SyncedEntityTypeList(entities);
        this.blockEntities = new SyncedBlockEntityTypeList();
    }

    @Override
    public void cloneInto(AbstractModeConfig cfg) {
        if (!(cfg instanceof ESPConfig other))
            throw new IllegalArgumentException("Can't clone config from another type!");

        super.cloneInto(cfg);

        this.tracer = other.tracer;
        if (other.entities == null) {
            this.entities = new SyncedEntityTypeList();
        } else {
            this.entities = other.entities.clone();
        }
        if (other.blockEntities == null) {
            this.blockEntities = new SyncedBlockEntityTypeList();
        } else {
            this.blockEntities = other.blockEntities.clone();
        }
    }

    public SyncedEntityTypeList getEntities() {
        return entities;
    }

    public SyncedBlockEntityTypeList getBlockEntities() {
        return blockEntities;
    }

    public boolean hasTracer() {
        return tracer;
    }

    public void setTracer(boolean tracer) {
        this.tracer = tracer;
    }

    @Override
    public ESPConfig clone() {
        return new ESPConfig(this);
    }

    public boolean shouldTag(EntityType<?> type) {
        if (!isEnabled())
            return false;
        Identifier id = Registries.ENTITY_TYPE.getId(type);
        if (id == null) {
            return false;
        }
        return entities.contains(id.toString());
    }
    public boolean shouldTag(BlockEntityType<?> type) {
        if (!isEnabled())
            return false;
        Identifier id = Registries.BLOCK_ENTITY_TYPE.getId(type);
        if (id == null) {
            return false;
        }
        return blockEntities.contains(id.toString());
    }
}
