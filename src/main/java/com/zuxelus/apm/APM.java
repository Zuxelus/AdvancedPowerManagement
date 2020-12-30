package com.zuxelus.apm;

import org.apache.logging.log4j.Logger;

import com.zuxelus.apm.init.ModItems;
import com.zuxelus.apm.network.ChannelHandler;
import com.zuxelus.apm.recipes.APMRecipes;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;

@Mod(name = APM.NAME, modid = APM.MODID, version = APM.VERSION, dependencies = "required-after:IC2", acceptedMinecraftVersions = "[1.7.10]")
public class APM {
	public static final String MODID = "advancedpowermanagement";
	public static final String NAME = "Advanced Power Management";
	public static final String VERSION = "@VERSION@";
	public static final String[] PREFIX = { "lv", "mv", "hv", "ev" };

	@SidedProxy(clientSide = "com.zuxelus.apm.ClientProxy", serverSide = "com.zuxelus.apm.ServerProxy")
	public static ServerProxy proxy;
	@Instance(MODID)
	public static APM instance;

	public static CreativeTab creativeTab = new CreativeTab();

	public static Logger logger;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		logger = event.getModLog();

		ChannelHandler.init();

		ModItems.onBlockRegistry();
		ModItems.onItemRegistry();
		ModItems.registerTileEntities();
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		NetworkRegistry.INSTANCE.registerGuiHandler(instance, proxy);
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		
	}
}
