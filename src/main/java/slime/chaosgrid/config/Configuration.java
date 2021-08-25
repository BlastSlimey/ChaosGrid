package slime.chaosgrid.config;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
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
		public final BooleanValue putLootInChests;
		public final ConfigValue<List<? extends String>> blockblacklist, itemblacklist;
		
		private Common(ForgeConfigSpec.Builder builder) {
			
			blocksPerChunk = builder
					.comment("The amount of blocks the game should try to spawn into every chunk (excluding the extraBlock)")
					.defineInRange("blocksPerChunk", 16, 0, Integer.MAX_VALUE);
			
			minHeight = builder
					.comment("The minimum height (inclusive) blocks can spawn at (must be lower than or equal to maxHeight)")
					.defineInRange("minHeight", 61, 0, 255);
			
			maxHeight = builder
					.comment("The maximum height (inclusive) blocks can spawn at (must be greater than or equal to minHeight)")
					.defineInRange("maxHeight", 64, 0, 255);
			
			extraBlock = builder
					.comment("A block that the game tries to spawn in every chunk at least once",
							"It is not affected by the blacklist")
					.define("extraBlock", "minecraft:grass_block");
			
			putLootInChests = builder
					.comment("(WIP) If true, every chest contains random items")
					.define("putLootInChests", true);
			
			List<String> blockblacklistdefault = Arrays.asList("minecraft:bedrock", "minecraft:air", "minecraft:fire", 
					"minecraft:soul_fire", "minecraft:end_portal", "minecraft:end_portal_frame", "minecraft:dragon_egg", 
					"minecraft:command_block", "minecraft:barrier", "minecraft:light", "minecraft:end_gateway", 
					"minecraft:repeating_command_block", "minecraft:chain_command_block", "minecraft:structure_void", 
					"minecraft:void_air", "minecraft:cave_air", "minecraft:bubble_column", "minecraft:jigsaw"),
					itemblacklistdefault = Arrays.asList("minecraft:air", "minecraft:bedrock");
			
			blockblacklist = builder
					.comment("Blacklist of spawnable blocks")
					.defineListAllowEmpty(split("blockblacklist"), () -> blockblacklistdefault, (o) -> o instanceof String);
			
			itemblacklist = builder
					.comment("(WIP) Blacklist of items that can appear in chests")
					.defineListAllowEmpty(split("itemblacklist"), () -> itemblacklistdefault, (o) -> o instanceof String);
			
		}
		
	}
	
	private static List<String> split(String path) {
		
        return Lists.newArrayList(DOT_SPLITTER.split(path));
        
    }
	
}
