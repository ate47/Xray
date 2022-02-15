package fr.atesab.xray.utils;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class LocationUtils {
	
	private static final ThreadLocal<NumberFormat> localTwoDigitNfFormat = new ThreadLocal<NumberFormat>() {
		@Override
		protected NumberFormat initialValue() {
			return new DecimalFormat("00");
		}
	};

	public static BlockPos getLookingBlockPos(Minecraft mc) {
		BlockPos answer = null;
		HitResult hitResult = mc.hitResult;
		if (hitResult.getType() == HitResult.Type.BLOCK && hitResult instanceof BlockHitResult) {
				answer = ((BlockHitResult)hitResult).getBlockPos();
		} else {
				Vec3 pos = hitResult.getLocation();
				answer = new BlockPos(pos.x, pos.y, pos.z);
		}
		return answer;
	}
	
	public static BlockPos getLookingFaceBlockPos(Minecraft mc) {
		BlockPos answer = null;
		Vec3 pos = mc.hitResult.getLocation();
		if (pos.x == (int)pos.x) {
			answer = getLookingBlockPos(mc).offset(mc.player.getX() < pos.x ? -1 : 1, 0, 0);
		} else if (pos.y == (int)pos.y) {
			answer = getLookingBlockPos(mc).offset(0, mc.player.getY() < pos.y ? -1 : 1, 0);
		} else if (pos.z == (int)pos.z) {
			answer = getLookingBlockPos(mc).offset(0, 0, mc.player.getZ() < pos.z ? -1 : 1);
		} else {
    		answer = new BlockPos(pos.x, pos.y, pos.z);
		}
		return answer;
	}
	
	public static NumberFormat getTwoDigitNumberFormat() {
		return localTwoDigitNfFormat.get();
	}

	public static String isSlimeChunk(Minecraft mc, ChunkPos chunk) {
		return String.valueOf(WorldgenRandom.seedSlimeChunk(chunk.x, chunk.z, 
				mc.getSingleplayerServer().getWorldData().worldGenSettings().seed(), 987234911L).nextInt(10) == 0);
	}
}
