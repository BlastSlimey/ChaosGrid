package slime.chaosgrid.world;

import net.minecraft.core.Registry;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraftforge.common.world.ForgeWorldType.IBasicChunkGeneratorFactory;

public class ChaosGridFactory implements IBasicChunkGeneratorFactory {

	@Override
	public ChunkGenerator createChunkGenerator(
			Registry<Biome> biomeRegistry, Registry<NoiseGeneratorSettings> dimensionSettingsRegistry, long seed) {
		
		return new ChaosGridChunkGenerator(biomeRegistry, seed);
		
	}

}
