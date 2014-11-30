package me.capit.mechanization;

import java.io.File;
import java.util.HashMap;
import java.util.logging.Logger;

import me.capit.mechanization.factory.MechaFactory;
import me.capit.mechanization.factory.WorldFactory;
import me.capit.mechanization.item.MechaItem;
import me.capit.mechanization.recipe.MechaFactoryRecipe;

import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.java.JavaPlugin;

public class Mechanization extends JavaPlugin {
	public static HashMap<String, MechaItem> items = new HashMap<String, MechaItem>();
	public static HashMap<String, MechaFactoryRecipe> recipes = new HashMap<String, MechaFactoryRecipe>();
	public static HashMap<String, MechaFactory> factories = new HashMap<String, MechaFactory>();
	public static Logger logger; public static ConsoleCommandSender console;
	public static File pluginDir; public static File itemDir;
	public static File recipeDir; public static File factoryDir;
	
	@Override
	public void onEnable(){
		logger = getLogger();
		console = getServer().getConsoleSender();
		
		console.sendMessage(ChatColor.WHITE+"---- "+ChatColor.YELLOW+"Mechanization"+ChatColor.WHITE+" -------------------");
		
		console.sendMessage(ChatColor.WHITE+"Initializing directories and loading defaults...");
		pluginDir = getDataFolder();
		saveResource("items", false);
		saveResource("recipes", false);
		saveResource("factories", false);
		
		itemDir = new File(pluginDir.getPath()+File.separator+"/items");
		recipeDir = new File(pluginDir.getPath()+File.separator+"/recipes");
		factoryDir = new File(pluginDir.getPath()+File.separator+"/factories");
		
		console.sendMessage(ChatColor.WHITE+"Loading items...");
		for (File f : itemDir.listFiles()){
			if (f.isFile()){
				MechaItem mi = new MechaItem(f);
				items.put(mi.getName(), mi);
				console.sendMessage(ChatColor.WHITE+"  Loaded "+ChatColor.translateAlternateColorCodes('&', "&o"+mi.getDisplayName()));
			}
		}
		
		console.sendMessage(ChatColor.WHITE+"Loading recipes...");
		for (File f : recipeDir.listFiles()){
			if (f.isFile()){
				MechaFactoryRecipe mi = new MechaFactoryRecipe(f);
				recipes.put(mi.getName(), mi);
				console.sendMessage(ChatColor.WHITE+"  Loaded "+ChatColor.translateAlternateColorCodes('&', mi.getDisplayName()));
			}
		}
		
		console.sendMessage(ChatColor.WHITE+"Loading factories...");
		for (File f : factoryDir.listFiles()){
			if (f.isFile()){
				MechaFactory mi = new MechaFactory(f);
				if (mi.formatMatchesDims()){
					factories.put(mi.getName(), mi);
					console.sendMessage(ChatColor.WHITE+"  Loaded "+ChatColor.translateAlternateColorCodes('&', mi.getDisplayName()));
				} else {
					console.sendMessage(ChatColor.WHITE+"  Skipped "+ChatColor.translateAlternateColorCodes('&', mi.getDisplayName())+" as format &cfailed&f.");
				}
			}
		}
		
		
	}
	
	@Override
	public void onDisable(){
		
	}
	
}
