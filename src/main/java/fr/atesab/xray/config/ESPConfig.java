package fr.atesab.xray.config;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;

public class ESPConfig extends AbstractModeConfig implements Cloneable {
    @Expose
    private List<String> entities = new ArrayList<>();
    @Expose
    private boolean tracer = false;

    private ESPConfig(ESPConfig other) {
        super(other);
        this.tracer = other.tracer;
        this.entities.addAll(other.entities);
    }

    public List<String> getEntities() {
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
}
