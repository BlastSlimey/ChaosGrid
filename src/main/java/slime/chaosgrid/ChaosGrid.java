package slime.chaosgrid;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.world.ForgeWorldType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fmlserverevents.FMLServerStartingEvent;
import slime.chaosgrid.config.Configuration;
import slime.chaosgrid.world.ChaosGridChunkGenerator;
import slime.chaosgrid.world.ChaosGridFactory;
import slime.chaosgrid.world.ChaosGridWorldType;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("chaosgrid")
public class ChaosGrid {
	
    public static final Logger LOGGER = LogManager.getLogger();

    public ChaosGrid() {
    	
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::enqueueIMC);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::processIMC);

        MinecraftForge.EVENT_BUS.register(this);
        
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Configuration.COMMON_SPEC);
        
    }

    private void setup(final FMLCommonSetupEvent event) {
    	
    	Registry.register(Registry.CHUNK_GENERATOR, new ResourceLocation("chaosgrid", "chaosgrid"), ChaosGridChunkGenerator.CODEC);
    	
    }

    private void enqueueIMC(final InterModEnqueueEvent event) {
    	
    }

    private void processIMC(final InterModProcessEvent event) {
    	
    	ChaosGridChunkGenerator.processRegistries();
    	
    }
    
    @SubscribeEvent
    public void onServerStarting(FMLServerStartingEvent event) {
        
    }
    
    @Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {
    	
        @SubscribeEvent
        public static void onWorldTypeRegistry(final RegistryEvent.Register<ForgeWorldType> event) {
        	
        	event.getRegistry().register(new ChaosGridWorldType(new ChaosGridFactory())
        			.setRegistryName(new ResourceLocation("chaosgrid", "chaosgrid")));
        	
        }
        
    }
    
}
