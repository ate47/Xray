package fr.atesab.xray.utils.esp;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Cache to store {@link ESPBlock} with their chunk
 */
public class ChunkCache {
    private final Map<Long, Map<Long, ESPBlock>> cache = Collections.synchronizedMap(new HashMap<>());

    /**
     * add a block to the cache
     *
     * @param b the block
     */
    public void addBlock(ESPBlock b) {
        long cid = b.chunkId();

        Map<Long, ESPBlock> chunkMap = cache.computeIfAbsent(cid, key -> Collections.synchronizedMap(new HashMap<>()));

        chunkMap.put(b.id(), b);
    }

    /**
     * remove all blocks from a particular location
     *
     * @param x x
     * @param y y
     * @param z z
     */
    public void removeBlock(int x, int y, int z) {
        locateBlock(x, y, z).forEach(this::removeBlock);
    }

    /**
     * remove a block
     *
     * @param b block
     */
    public void removeBlock(ESPBlock b) {
        long cid = b.chunkId();

        Map<Long, ESPBlock> chunkMap = cache.computeIfAbsent(cid, key -> Collections.synchronizedMap(new HashMap<>()));

        chunkMap.remove(b.id());
    }

    /**
     * locate all the blocks from a particular location
     *
     * @param x location x
     * @param y location y
     * @param z location z
     * @return stream of blocks
     */
    public Stream<ESPBlock> locateBlock(int x, int y, int z) {
        long cid = ESPBlock.chunkId(x, z);
        Map<Long, ESPBlock> chunkMap = cache.get(cid);

        if (chunkMap == null) {
            // no blocks in this chunk
            return Stream.empty();
        }

        return chunkMap.values().stream()
                .filter(b -> b.matchLocation(x, y, z));
    }

    /**
     * @return all the blocks in the cache
     */
    public Stream<ESPBlock> getBlocks() {
        return cache.values().stream().flatMap(chunkMap -> chunkMap.values().stream());
    }

    /**
     * get all the block in a particular radius
     *
     * @param x         center location x
     * @param y         center location y
     * @param z         center location z
     * @param maxRadius maximum radius
     * @return stream of blocks
     */
    public Stream<ESPBlock> getBlocks(int x, int y, int z, int maxRadius) {
        int squaredRadius = maxRadius * maxRadius;

        return cache.entrySet().stream().flatMap(e -> {
            long cid = e.getKey();

            // get chunk center location
            int ccx = ESPBlock.chunkX(cid) << 4 + 8;
            int ccz = ESPBlock.chunkZ(cid) << 4 + 8;

            int squaredDistanceToChunk = (x - ccx) * (x - ccx) + (z - ccz) * (z - ccz);

            // sqrt(2) ~= 3 / 2 "math"
            if (squaredDistanceToChunk + 8 * 3 / 2 > squaredRadius) {
                // ignore this chunk, too far
                return Stream.empty();
            }

            return e.getValue().values().stream()
                    .filter(b -> {
                        int bx = b.x();
                        int by = b.y();
                        int bz = b.z();
                        int squaredDistance = (bx - x) * (bx - x) + (by - y) * (by - y) + (bz - z) * (bz - z);
                        return squaredDistance <= squaredRadius;
                    });
        });
    }

    /**
     * clear all the cache
     */
    public void clear() {
        cache.clear();
    }

    /**
     * clear a chunk from the cache
     *
     * @param x chunk x location
     * @param z chunk y location
     */
    public void clearChunk(int x, int z) {
        cache.remove(ESPBlock.chunkIdFromChunkLocation(x, z));
    }
}
