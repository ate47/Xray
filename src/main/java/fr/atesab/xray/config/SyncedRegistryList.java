package fr.atesab.xray.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import fr.atesab.xray.utils.TagOnWriteList;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class SyncedRegistryList<R> extends TagOnWriteList<String> implements Cloneable {

    private List<R> objects = new ArrayList<>();
    private final Registry<R> registry;
    private boolean synced = false;

    public SyncedRegistryList(Registry<R> registry) {
        super(new ArrayList<>());
        this.registry = registry;
    }

    protected SyncedRegistryList(SyncedRegistryList<R> other) {
        super(new ArrayList<>(other));
        registry = other.registry;
        if (other.synced) {
            this.objects.addAll(other.objects);
            synced = true;
        }
    }

    public SyncedRegistryList(R[] objects, Registry<R> registry) {
        this(Arrays.asList(objects), registry);
    }

    public SyncedRegistryList(List<R> objects, Registry<R> registry) {
        super(new ArrayList<>());
        this.registry = registry;
        setObjects(objects);
    }

    public List<R> getObjects() {
        if (!synced)
            sync();
        return objects;
    }

    public void setObjects(List<? extends R> objects) {
        setTagEnabled(false);
        this.objects = new ArrayList<>(objects);
        clear();
        objects.stream().filter(Objects::nonNull).map(registry::getId).map(Object::toString).forEach(this::add);
        setTagEnabled(true);
        synced = true;
    }

    public SyncedRegistryList<R> sync() {
        objects.clear();
        stream().map(Identifier::new).map(registry::get).filter(Objects::nonNull).forEach(objects::add);
        removeUpdated();
        synced = true;
        return this;
    }

    @Override
    protected void onUpdate() {
        if (synced)
            sync();
    }

    @Override
    public SyncedRegistryList<R> clone() {
        return new SyncedRegistryList<R>(this);
    }
}