package fr.atesab.xray.config;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

import com.google.gson.annotations.Expose;

import fr.atesab.xray.color.EntityTypeIcon;
import fr.atesab.xray.color.EnumElement;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class ESPConfig extends AbstractModeConfig implements Cloneable {
    public enum Template implements EnumElement {
        // @formatter:off
        BLANK("x13.mod.template.blank", new ItemStack(Items.PAPER), new ESPConfig()),
        PLAYER("x13.mod.esp.template.player", new ItemStack(Items.PLAYER_HEAD), 
            new ESPConfig(0, 0, "Player", EntityType.PLAYER)
        ),
        WITHER("x13.mod.esp.template.wither", new ItemStack(Items.WITHER_SKELETON_SKULL), 
            new ESPConfig(0, 0, "Wither", EntityType.WITHER, EntityType.WITHER_SKELETON)
        ),
        AGGRESIVE("x13.mod.esp.template.aggresive", new ItemStack(Items.CREEPER_HEAD), () -> 
             new ESPConfig(EntityTypeIcon.getEntityOfType(MobCategory.MONSTER))
        ),
        PASSIVE("x13.mod.esp.template.passive", new ItemStack(Items.POPPY), () -> 
             new ESPConfig(EntityTypeIcon.getEntityOfType(MobCategory.CREATURE))
        );
        // @formatter:on

        private Component title;
        private ItemStack icon;
        private Supplier<ESPConfig> cfg;

        Template(String translation, ItemStack icon, ESPConfig cfg) {
            this(translation, icon, () -> cfg);
        }

        Template(String translation, ItemStack icon, Supplier<ESPConfig> cfg) {
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
    private boolean tracer = false;

    public ESPConfig() {
        this(Collections.emptyList());
    }

    private ESPConfig(ESPConfig other) {
        super(other);
    }

    public ESPConfig(EntityType<?>... entities) {
        super();
        this.entities = new SyncedEntityTypeList(entities);
    }

    public ESPConfig(List<EntityType<?>> entities) {
        super();
        this.entities = new SyncedEntityTypeList(entities);
    }

    public ESPConfig(int key, int ScanCode, String name, EntityType<?>... entities) {
        super(key, ScanCode, name);
        this.entities = new SyncedEntityTypeList(entities);
    }

    @Override
    public void cloneInto(AbstractModeConfig cfg) {
        if (!(cfg instanceof ESPConfig other))
            throw new IllegalArgumentException("Can't clone config from another type!");

        super.cloneInto(cfg);

        this.tracer = other.tracer;
        this.entities = other.entities.clone();
    }

    public SyncedEntityTypeList getEntities() {
        return entities;
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

    @SuppressWarnings("deprecation")
    public boolean shouldTag(EntityType<?> type) {
        if (!isEnabled())
            return false;
        String name = Registry.ENTITY_TYPE.getKey(type).toString();
        return entities.contains(name);
    }
}
