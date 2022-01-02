package fr.atesab.xray.config;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;

public class ESPConfig extends AbstractModeConfig {
    @Expose
    private List<String> entities = new ArrayList<>();
    @Expose
    private boolean tracer = false;

    public List<String> getEntities() {
        return entities;
    }

    public boolean hasTracer() {
        return tracer;
    }

    public void setTracer(boolean tracer) {
        this.tracer = tracer;
    }
}
