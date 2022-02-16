package fr.atesab.xray.utils;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.ChunkRandom;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class LocationUtils {
	
	private static final ThreadLocal<NumberFormat> localTwoDigitNfFormat = ThreadLocal.withInitial(() -> new DecimalFormat("00"));

	public static BlockPos getLookingBlockPos(MinecraftClient mc) {
		HitResult hitResult = mc.crosshairTarget;
		if (hitResult instanceof BlockHitResult bir) {
			return bir.getBlockPos();
		} else if (hitResult != null) {
				Vec3d pos = hitResult.getPos();
			return new BlockPos(pos.x, pos.y, pos.z);
		} else {
			return new BlockPos(0, 0, 0);
		}
	}
	
	public static BlockPos getLookingFaceBlockPos(MinecraftClient mc, ClientPlayerEntity player) {
		HitResult target = mc.crosshairTarget;
		if (target == null) {
			return new BlockPos(0, 0, 0);
		}
		Vec3d pos = target.getPos();
		if (pos.x == (int)pos.x) {
			return getLookingBlockPos(mc).add(player.getX() < pos.x ? -1 : 1, 0, 0);
		} else if (pos.y == (int)pos.y) {
			return getLookingBlockPos(mc).add(0, player.getY() < pos.y ? -1 : 1, 0);
		} else if (pos.z == (int)pos.z) {
			return getLookingBlockPos(mc).add(0, 0, player.getZ() < pos.z ? -1 : 1);
		} else {
			return new BlockPos(pos.x, pos.y, pos.z);
		}
	}
	
	public static NumberFormat getTwoDigitNumberFormat() {
		return localTwoDigitNfFormat.get();
	}

	public static String isSlimeChunk(MinecraftClient mc, ChunkPos chunk) {
		IntegratedServer server = mc.getServer();
		if (server == null) {
			return "false";
		}
		return String.valueOf(ChunkRandom.getSlimeRandom(chunk.x, chunk.z,
				server.getSaveProperties().getGeneratorOptions().getSeed(), 987234911L).nextInt(10) == 0);
	}
}
