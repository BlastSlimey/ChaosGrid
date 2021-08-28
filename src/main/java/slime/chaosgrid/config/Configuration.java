package slime.chaosgrid.config;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;

public class Configuration {
	
	private static final Splitter DOT_SPLITTER = Splitter.on(".");
	public static final ForgeConfigSpec COMMON_SPEC;
	public static final Common COMMON;
	
	static {
		
		Pair<Common,ForgeConfigSpec> pair = new ForgeConfigSpec.Builder().configure(Common::new);
		
		COMMON_SPEC = pair.getRight();
		COMMON = pair.getLeft();
		
	}
	
	public static class Common {
		
		public final IntValue blocksPerChunk, minHeight, maxHeight;
		public final ConfigValue<String> extraBlock;
//		public final BooleanValue putLootInChests;
		public final ConfigValue<List<? extends String>> blockBlacklist, /*itemBlacklist, featureBlacklist,*/ structureFeatureBlacklist;
		
		public final int blocksPerChunkDefault = 48,
				minHeightDefault = 62,
				maxHeightDefault = 64;
		public final String extraBlockDefault = "minecraft:grass_block";
		public final boolean putLootInChestsDefault = true;
		public final String[] blockblacklistDefault = new String[] {"minecraft:bedrock", "minecraft:air", "minecraft:fire", 
				"minecraft:soul_fire", "minecraft:end_portal", "minecraft:end_portal_frame", "minecraft:dragon_egg", 
				"minecraft:command_block", "minecraft:barrier", "minecraft:light", "minecraft:end_gateway", 
				"minecraft:repeating_command_block", "minecraft:chain_command_block", "minecraft:structure_void", 
				"minecraft:void_air", "minecraft:cave_air", "minecraft:bubble_column", "minecraft:jigsaw", 
				"minecraft:nether_portal"},
				itemBlacklistDefault = new String[] {},
				featureBlacklistDefault = new String[] {"minecraft:underwater_magma", "minecraft:", "minecraft:", "minecraft:", "minecraft:", "minecraft:", "minecraft:", "minecraft:", "minecraft:", "minecraft:", "minecraft:", "minecraft:", "minecraft:", "minecraft:", "minecraft:", "minecraft:", "minecraft:", "minecraft:", "minecraft:", "minecraft:", "minecraft:"},
				structureFeatureBlacklistDefault = new String[] {"minecraft:mineshaft", "minecraft:mineshaft_mesa", 
						"minecraft:swamp_hut", "minecraft:village_plains", "minecraft:village_desert", 
						"minecraft:village_savanna", "minecraft:village_snowy", "minecraft:village_taiga"};
		
		private Common(ForgeConfigSpec.Builder builder) {
			
			blocksPerChunk = builder
					.comment("The amount of blocks the game should try to spawn into every chunk (excluding the extraBlock)")
					.defineInRange("blocksPerChunk", blocksPerChunkDefault, 0, Integer.MAX_VALUE);
			
			minHeight = builder
					.comment("_", "The minimum height (inclusive) blocks can spawn at (must be lower than or equal to maxHeight)")
					.defineInRange("minHeight", minHeightDefault, 0, 255);
			
			maxHeight = builder
					.comment("_", "The maximum height (inclusive) blocks can spawn at (must be greater than or equal to minHeight)")
					.defineInRange("maxHeight", maxHeightDefault, 0, 255);
			
			extraBlock = builder
					.comment("_", "A block that the game tries to spawn in every chunk at least once",
							"It is not affected by the blacklist")
					.define("extraBlock", extraBlockDefault);
			
//			putLootInChests = builder
//					.comment("_", "(WIP) If true, every chest contains random items")
//					.define("putLootInChests", putLootInChestsDefault);
			
			blockBlacklist = builder
					.comment("_", "Blacklist of spawnable blocks")
					.worldRestart()
					.defineListAllowEmpty(split("blockblacklist"), blacklistSupplier(blockblacklistDefault), Configuration::isString);
			
//			itemBlacklist = builder
//					.comment("_", "(WIP) Blacklist of items that can appear in chests")
//					.defineListAllowEmpty(split("itemblacklist"), blacklistSupplier(itemBlacklistDefault), Configuration::isString);
			
//			featureBlacklist = builder
//					.comment("_", "Blacklist of generated features")
//					.defineListAllowEmpty(split("featureblacklist"), blacklistSupplier(featureBlacklistDefault), Configuration::isString);
			
			structureFeatureBlacklist = builder
					.comment("_", "Blacklist of generated structures")
					.worldRestart()
					.defineListAllowEmpty(split("structurefeatureblacklist"), blacklistSupplier(structureFeatureBlacklistDefault), Configuration::isString);
			
		}
		
	}
	
	private static boolean isString(Object o) {
		
		return o instanceof String;
		
	}
	
	private static Supplier<List<? extends String>> blacklistSupplier(String[] list) {
		
		return () -> Arrays.asList(list);
		
	}
	
	private static List<String> split(String path) {
		
        return Lists.newArrayList(DOT_SPLITTER.split(path));
        
    }
	
}
