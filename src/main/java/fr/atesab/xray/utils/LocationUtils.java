package fr.atesab.xray.utils;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class LocationUtils {
	
	private static final ThreadLocal<NumberFormat> localTwoDigitNfFormat = ThreadLocal.withInitial(() -> new DecimalFormat("00"));

	public static BlockPos getLookingBlockPos(Minecraft mc) {
		HitResult hitResult = mc.hitResult;
		if (hitResult instanceof BlockHitResult bir) {
			return bir.getBlockPos();
		} else if (hitResult != null) {
			Vec3 pos = hitResult.getLocation();
			return new BlockPos((int) pos.x, (int) pos.y, (int) pos.z);
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
			return new BlockPos((int) pos.x, (int) pos.y, (int) pos.z);
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
				server.getWorldData().worldGenOptions().seed(), 987234911L).nextInt(10) == 0);
	}
	
	public static String getDurabilityOrFoodData(ItemStack item) {
		if (item.isDamageableItem()) {
			return String.valueOf(getRemainDurability(item));
		} else if (item.isEdible()) {
			return String.valueOf(item.getFoodProperties(null).getNutrition()) + "(" + String.format("%.1f",getAddSaturation(item)) + ")";
		} else {
			return "-";
		}
	}

	public static String getMaxDurabilityOrAfterFoodData(ItemStack item,int currentNutrition, float currentSaturation) {
		if (item.isDamageableItem()) {
			return String.valueOf(item.getMaxDamage());
		} else if (item.isEdible()) {
			int afterNutrition = Math.min(currentNutrition + item.getFoodProperties(null).getNutrition(), 20);
			float afterSaturation = Math.min(currentSaturation + getAddSaturation(item), 20);
			return String.valueOf(afterNutrition) + "(" + String.format("%.1f",afterSaturation) + ")";
		} else {
			return "-";
		}
	}

	public static int getRemainDurability(ItemStack item) {
		return item.getMaxDamage() - item.getDamageValue();
	}

	public static float getAddSaturation(ItemStack item) {
		return item.getFoodProperties(null).getNutrition() * item.getFoodProperties(null).getSaturationModifier() * 2.0F;
	}
}
