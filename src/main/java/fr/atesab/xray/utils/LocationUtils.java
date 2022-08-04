package fr.atesab.xray.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class LocationUtils {
	
	private static final ThreadLocal<NumberFormat> localTwoDigitNfFormat = ThreadLocal.withInitial(() -> new DecimalFormat("00"));

	public static BlockPos getLookingBlockPos(Minecraft mc) {
		HitResult hitResult = mc.hitResult;
		if (hitResult instanceof BlockHitResult bir) {
			return bir.getBlockPos();
		} else if (hitResult != null) {
			Vec3 pos = hitResult.getLocation();
			return new BlockPos(pos.x, pos.y, pos.z);
		} else {
			return new BlockPos(0, 0, 0);
		}
	}
	
	public static BlockPos getLookingFaceBlockPos(Minecraft mc, LocalPlayer player) {
		HitResult target = mc.hitResult;
		if (target == null) {
			return new BlockPos(0, 0, 0);
		}
		Vec3 pos = target.getLocation();
		if (pos.x == (int)pos.x) {
			return getLookingBlockPos(mc).offset(player.getX() < pos.x ? -1 : 1, 0, 0);
		} else if (pos.y == (int)pos.y) {
			return getLookingBlockPos(mc).offset(0, player.getY() < pos.y ? -1 : 1, 0);
		} else if (pos.z == (int)pos.z) {
			return getLookingBlockPos(mc).offset(0, 0, player.getZ() < pos.z ? -1 : 1);
		} else {
			return new BlockPos(pos.x, pos.y, pos.z);
		}
	}
	
	public static NumberFormat getTwoDigitNumberFormat() {
		return localTwoDigitNfFormat.get();
	}

	public static String isSlimeChunk(Minecraft mc, ChunkPos chunk) {
		IntegratedServer server = mc.getSingleplayerServer();
		if (server == null) {
			return "false";
		}
		return String.valueOf(WorldgenRandom.seedSlimeChunk(chunk.x, chunk.z,
				server.getWorldData().worldGenSettings().seed(), 987234911L).nextInt(10) == 0);
	}
}
