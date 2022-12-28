package fr.atesab.xray.utils.esp;

import fr.atesab.xray.config.BlockConfig;

import java.util.ArrayList;
import java.util.List;

public class ESPCache {
    private final List<BlockConfig> cfg = new ArrayList<>();

    public void loadConfig(List<BlockConfig> cfg) {
        this.cfg.clear();
        this.cfg.addAll(cfg);
    }

    public void clear() {
        // TODO: impl
    }
}
