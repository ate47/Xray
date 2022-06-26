package fr.atesab.xray.config;

import fr.atesab.xray.utils.LocationUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.Vec3;

//this class is Singleton.
public class CurrentPlayerInfoHolder {
	private Minecraft mc = Minecraft.getInstance();
	private Player player;
	private Level level;
	private Vec3 playerPosition;
	private BlockPos playerBlockPosition;
	private ChunkPos playerChunk;
	private BlockPos lookingBlockPos;
	private Block lookingBlock;
	private Direction playerDirection;
	private double x;
	private double y;
	private double z;
	private int fx;
	private int fy;
	private int fz;
	private int cx;
	private int cz;
	private String playerName;
	private Holder<Biome> biome;
	private long dayTime;
	private double timeOfDay;
	private long days;
	private int hours;
	private int minutes;
	private int seconds;
	
	public CurrentPlayerInfoHolder() {
		update();
	}
	
	public void update() {
		if (mc.player == null)
				return;
		player = mc.player;
		level = mc.level;
		playerPosition = player.position();
		x = playerPosition.x;
		y = playerPosition.y;
		z = playerPosition.z;
		fx = (int)x;
		fy = (int)y;
		fz = (int)z;
		playerChunk = player.chunkPosition();
		playerBlockPosition = player.blockPosition();
		playerName = player.getGameProfile().getName();
		biome = level.getBiome(playerBlockPosition); //getBiome
		cx = playerChunk.x;
		cz = playerChunk.z;
		lookingBlockPos = LocationUtils.getLookingBlockPos(mc);
		lookingBlock = level.getBlockState(lookingBlockPos).getBlock();
		playerDirection = player.getDirection();
		dayTime = level.getDayTime();
		timeOfDay = (dayTime % 24000) / 24000.0;
		days = dayTime / 24000;
		hours = (int)Math.floor((dayTime + 6000) % 24000 / 1000);
		double hourMinutes = (dayTime % 1000) / 1000.0;
		//hourMinutes must be less than 1, but sometimes It's over 1.
		minutes = Math.min((int)Math.floor(hourMinutes * 60),59);
		seconds = Math.min((int)Math.floor((hourMinutes * 3600) % 60), 59);
	}

	public Player getPlayer() {
		return player;
	}

	public Level getLevel() {
		return level;
	}

	public Vec3 getPlayerPosition() {
		return playerPosition;
	}

	public BlockPos getPlayerBlockPos() {
		return playerBlockPosition;
	}

	public ChunkPos getPlayerChunkPos() {
		return playerChunk;
	}

	public BlockPos getLookingBlockPos() {
		return lookingBlockPos;
	}

	public Block getLookingBlock() {
		return lookingBlock;
	}

	public Direction getPlayerDirection() {
		return playerDirection;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}
	public double getZ() {
		return z;
	}

	public int getFloatedX() {
		return fx;
	}

	public int getFloatedY() {
		return fy;
	}

	public int getFloatedZ() {
		return 	fz;
	}

	public int getPlayerChunkX() {
		return cx;
	}

	public int getPlayerChunkZ() {
		return cz;
	}

	public String getPlayerName() {
		return playerName;
	}

	public Holder<Biome> getCurrentBiome() {
		return biome;
	}

	public long getMinecraftDayTime() {
		return dayTime;
	}

	public double getTimeOfDay() {
		return timeOfDay;
	}

	public long getDays() {
		return days;
	}

	public long getHours() {
		return hours;
	}

	public long getMinutes() {
		return minutes;
	}

	public long getSeconds() {
		return seconds;
	}
	
	public int getBrightness(LightLayer layer) {
		return level.getBrightness(layer, playerBlockPosition);
	}

	public int getLookingBrightness(LightLayer layer) {
		return level.getBrightness(layer, LocationUtils.getLookingFaceBlockPos(mc));
	}
}
