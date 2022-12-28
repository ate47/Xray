package fr.atesab.xray.utils.esp;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

public final class ESPBlock {
    public static long chunkIdFromChunkLocation(int x, int z) {
        return x | ((long) z << 28);
    }

    public static int chunkX(long cid) {
        return (int) (cid & ((1L << 28) - 1));
    }

    public static int chunkZ(long cid) {
        return (int) (cid >>> 28);
    }

    public static long chunkId(int x, int z) {
        return chunkIdFromChunkLocation(x >>> 4, z >>> 4);
    }

    private static final AtomicLong BLOCK_ID = new AtomicLong();
    private final long id;
    private final int x;
    private final int y;
    private final int z;
    private final int color;
    private final boolean tracer;

    public ESPBlock(int x, int y, int z, int color, boolean tracer) {
        this.id = BLOCK_ID.incrementAndGet();
        this.x = x;
        this.y = y;
        this.z = z;
        this.color = color;
        this.tracer = tracer;
    }

    public long id() {
        return id;
    }

    public int x() {
        return x;
    }

    public int y() {
        return y;
    }

    public int z() {
        return z;
    }

    public long chunkId() {
        return chunkId(x, z);
    }

    public int color() {
        return color;
    }

    public boolean tracer() {
        return tracer;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (ESPBlock) obj;
        return this.id == that.id &&
                this.x == that.x &&
                this.y == that.y &&
                this.z == that.z &&
                this.color == that.color &&
                this.tracer == that.tracer;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, x, y, z, color, tracer);
    }

    @Override
    public String toString() {
        return "ESPBlock[" +
                "id=" + id + ", " +
                "x=" + x + ", " +
                "y=" + y + ", " +
                "z=" + z + ", " +
                "color=" + color + ", " +
                "tracer=" + tracer + ']';
    }

    public boolean matchLocation(int x, int y, int z) {
        return this.x == x && this.y == y && this.z == z;
    }
}
