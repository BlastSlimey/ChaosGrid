package slime.chaosgrid.world;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import com.mojang.serialization.Codec;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.RegistryLookupCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.StructureFeatureManager;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.OverworldBiomeSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.level.levelgen.StructureSettings;
import net.minecraftforge.registries.ForgeRegistries;
import slime.chaosgrid.config.Configuration;

public class ChaosGridChunkGenerator extends ChunkGenerator {
	
	public static final Codec<ChaosGridChunkGenerator> CODEC = RegistryLookupCodec.create(Registry.BIOME_REGISTRY).xmap(ChaosGridChunkGenerator::new, ChaosGridChunkGenerator::biomes).stable().codec();
	private static BlockState[] blockstates; 
	private static Item[] items;
	private long seed = 123;
	private final Registry<Biome> biomes;

	public ChaosGridChunkGenerator(Registry<Biome> biomes, long seed) {
		
		super(new OverworldBiomeSource(seed, false, false, biomes), new StructureSettings(true));
		this.biomes = biomes;
		this.seed = seed;
		
	}
	
	public ChaosGridChunkGenerator(Registry<Biome> biomes) {
		
		super(new OverworldBiomeSource(123, false, false, biomes), new StructureSettings(true));
		this.biomes = biomes;
		
	}

	   public Registry<Biome> biomes() {
	      return this.biomes;
	   }

	@Override
	protected Codec<? extends ChunkGenerator> codec() {
		return CODEC;
	}

	@Override
	public ChunkGenerator withSeed(long seed) {
		return new ChaosGridChunkGenerator(biomes, seed);
	}

	@Override
	public void buildSurfaceAndBedrock(WorldGenRegion region, ChunkAccess chunk) {
		
	}

	@Override
	public CompletableFuture<ChunkAccess> fillFromNoise(Executor p_156171_, StructureFeatureManager p_156172_, ChunkAccess chunk) {
		
		ChunkPos cpos = chunk.getPos();
		Random r = new Random((int) (Math.cos(cpos.x)*seed) + (cpos.z << 1));
		boolean putloot = Configuration.COMMON.putLootInChests.get();
		BlockPos nextpos;
		BlockState state, extrablock = ForgeRegistries.BLOCKS.getValue(
				new ResourceLocation(Configuration.COMMON.extraBlock.get())).defaultBlockState();
		int bpc = Configuration.COMMON.blocksPerChunk.get(),
				min = Configuration.COMMON.minHeight.get(),
				max = Configuration.COMMON.maxHeight.get();
		
		if (min > max) {
			min = 61;
			max = 64;
		}
		
		nextpos = new BlockPos(r.nextInt(16), r.nextInt(max - min + 1) + min, r.nextInt(16));
		chunk.setBlockState(nextpos, extrablock, false);
//		if (putloot)
//			fillChest(chunk, nextpos, r);
		
		for (int a = 0; a < bpc; a++) {
			
			nextpos = new BlockPos(r.nextInt(16), r.nextInt(max - min + 1) + min, r.nextInt(16));
			state = blockstates[r.nextInt(blockstates.length)];
			chunk.setBlockState(nextpos, state, false);
//			if (putloot)
//				fillChest(chunk, nextpos, r);
			
		}
		
		return CompletableFuture.completedFuture(chunk);
		
	}
	
	private static void fillChest(ChunkAccess chunk, BlockPos pos, Random r) {

		BlockEntity entity = chunk.getBlockEntity(pos);
		if (entity instanceof RandomizableContainerBlockEntity) {
			int count = r.nextInt(4) + 2;
			for (int b = 0; b < count; b++)
				((RandomizableContainerBlockEntity) entity).setItem(
						r.nextInt(((RandomizableContainerBlockEntity) entity).getContainerSize()), 
						new ItemStack(() -> items[r.nextInt(items.length)]));
		}
		
	}

	@Override
	public int getBaseHeight(int p_156153_, int p_156154_, Types p_156155_, LevelHeightAccessor p_156156_) {
		return 0;
	}

	@Override
	public NoiseColumn getBaseColumn(int p_156150_, int p_156151_, LevelHeightAccessor p_156152_) {
		return null;
	}

	@Override
	public int getSpawnHeight(LevelHeightAccessor p_156157_) {
		return 64;
	}

	public static void processRegistries() {
		
		Collection<Block> col = ForgeRegistries.BLOCKS.getValues();
		Block[] blacklist = getBlockBlacklist(Configuration.COMMON.blockblacklist.get());
		ArrayList<BlockState> list = new ArrayList<>();
		
		col.forEach((block) -> {
			if (!isBlockBlacklist(block, blacklist))
				list.add(block.defaultBlockState());
		});
		
		blockstates = list.toArray(new BlockState[0]);
		
		Collection<Item> col2 = ForgeRegistries.ITEMS.getValues();
		Item[] blacklist2 = getItemBlacklist(Configuration.COMMON.itemblacklist.get());
		ArrayList<Item> list2 = new ArrayList<>();
		
		col2.forEach((item) -> {
			if (!isItemBlacklist(item, blacklist2))
				list2.add(item);
		});
		
		items = list2.toArray(new Item[0]);
		
	}
	
	private static boolean isBlockBlacklist(Block block, Block[] blacklist) {
		
		for (Block blblock : blacklist)
			if (block == blblock)
				return true;
		
		return false;
		
	}

	private static boolean isItemBlacklist(Item item, Item[] blacklist) {
		
		for (Item blitem : blacklist)
			if (item == blitem)
				return true;
		
		return false;
		
	}
	
	private static Block[] getBlockBlacklist(List<? extends String> blacklist) {
		
		Block[] newlist = new Block[blacklist.size()];
		Counter i = new Counter();
		blacklist.forEach((name) -> {
			newlist[i.iplusplus()] = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(name));
		});
		return newlist;
		
	}

	private static Item[] getItemBlacklist(List<? extends String> blacklist) {
		
		Item[] newlist = new Item[blacklist.size()];
		Counter i = new Counter();
		blacklist.forEach((name) -> {
			newlist[i.iplusplus()] = ForgeRegistries.ITEMS.getValue(new ResourceLocation(name));
		});
		return newlist;
		
	}
	
	private static class Counter {
		
		int i = 0;
		
		private int iplusplus() {
			return i++;
		}
		
	}
	
}
