package fr.atesab.xray.utils;

import fr.atesab.xray.config.BlockConfig;
import fr.atesab.xray.utils.esp.ESPCache;

import java.util.List;

public class BlockESPSystem {
    private boolean reloadAsked;
    private final ESPCache cache = new ESPCache();

    public void render() {

    }

    public void tick() {
        if (reloadAsked) {
            cache.clear();
            reloadAsked = false;
        }


    }

    public void reload() {
        this.reloadAsked = true;
    }

    public void reloadConfig(List<BlockConfig> cfg) {
        reload();
        cache.loadConfig(cfg);
    }
}
