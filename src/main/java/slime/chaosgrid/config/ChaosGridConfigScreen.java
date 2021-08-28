package slime.chaosgrid.config;

import java.util.Arrays;
import java.util.List;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.gui.entries.IntegerListEntry;
import me.shedaniel.clothconfig2.gui.entries.IntegerSliderEntry;
import me.shedaniel.clothconfig2.gui.entries.StringListEntry;
import me.shedaniel.clothconfig2.gui.entries.StringListListEntry;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import slime.chaosgrid.config.Configuration.Common;

public class ChaosGridConfigScreen {
	
	private ConfigBuilder builder;
	private Screen screen;
	
	public ChaosGridConfigScreen(Screen parent) {
		
		builder = ConfigBuilder.create()
				.setParentScreen(parent)
				.setTitle(translate("title"));
		
		fillConfigScreen();
		
		builder.setSavingRunnable(this::saveConfig);
		
		screen = builder.build();
		
	}
	
	public Screen getScreen() {
		
		return screen;
		
	}
	
	private void saveConfig() {
		
		Configuration.COMMON_SPEC.save();
		
	}
	
	private void fillConfigScreen() {
		
		Common common = Configuration.COMMON;
		
		IntegerListEntry bpc = builder.entryBuilder()
				.startIntField(translate("bpc"), common.blocksPerChunk.get())
				.setDefaultValue(common.blocksPerChunkDefault)
				.setSaveConsumer((value) -> common.blocksPerChunk.set(value))
				.build();
		IntegerSliderEntry minheight = builder.entryBuilder()
				.startIntSlider(translate("minheight"), common.minHeight.get(), 0, 255)
				.setDefaultValue(common.minHeightDefault)
				.setSaveConsumer((value) -> common.minHeight.set(value))
				.build();
		IntegerSliderEntry maxheight = builder.entryBuilder()
				.startIntSlider(translate("maxheight"), common.maxHeight.get(), 0, 255)
				.setDefaultValue(common.maxHeightDefault)
				.setSaveConsumer((value) -> common.maxHeight.set(value))
				.build();
		StringListEntry extrablock = builder.entryBuilder()
				.startStrField(translate("extrablock"), common.extraBlock.get())
				.setDefaultValue(common.extraBlockDefault)
				.setSaveConsumer((value) -> common.extraBlock.set(value))
				.build();
		@SuppressWarnings("unchecked")
		StringListListEntry blockblacklist = builder.entryBuilder()
				.startStrList(translate("blockblacklist"), (List<String>) common.blockBlacklist.get())
				.setDefaultValue(Arrays.asList(common.blockblacklistDefault))
				.setSaveConsumer((value) -> common.blockBlacklist.set(value))
				.build();
		@SuppressWarnings("unchecked")
		StringListListEntry structureblacklist = builder.entryBuilder()
				.startStrList(translate("structureblacklist"), (List<String>) common.structureFeatureBlacklist.get())
				.setDefaultValue(Arrays.asList(common.structureFeatureBlacklistDefault))
				.setSaveConsumer((value) -> common.structureFeatureBlacklist.set(value))
				.build();
		
		ConfigCategory general = builder.getOrCreateCategory(translate("general"));
		general.addEntry(bpc)
				.addEntry(minheight).addEntry(maxheight)
				.addEntry(extrablock)
				.addEntry(blockblacklist).addEntry(structureblacklist);
		
	}
	
	private TranslatableComponent translate(String key) {
		
		return new TranslatableComponent("slime.chaosgrid.cloth." + key);
		
	}

}
