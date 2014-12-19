package me.capit.mechanization;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Logger;

import me.capit.mechanization.exception.MechaException;
import me.capit.mechanization.factory.MechaFactory;
import me.capit.mechanization.factory.WorldFactory;
import me.capit.mechanization.item.MechaItem;
import me.capit.mechanization.recipe.MechaFactoryRecipe;

import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.java.JavaPlugin;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

public class Mechanization extends JavaPlugin {
	public static HashMap<String, MechaItem> items = new HashMap<String, MechaItem>();
	public static HashMap<String, MechaFactoryRecipe> recipes = new HashMap<String, MechaFactoryRecipe>();
	public static HashMap<String, MechaFactory> factories = new HashMap<String, MechaFactory>();
	public static Logger logger; public static ConsoleCommandSender console;
	public static File pluginDir;
	
	public static Document factoriesDoc,itemsDoc,recipesDoc;
	
	public static Mechanization plugin;
	
	static {
		ConfigurationSerialization.registerClass(WorldFactory.class);
		ConfigurationSerialization.registerClass(Position3.class);
	}
	
	@Override
	public void onEnable(){
		logger = getLogger();
		console = getServer().getConsoleSender();
		plugin = this;
		
		console.sendMessage(ChatColor.WHITE+"---- "+ChatColor.YELLOW+"Mechanization"+ChatColor.WHITE+" -------------------");
		
		console.sendMessage(ChatColor.WHITE+"Initializing directories and loading defaults...");
		pluginDir = getDataFolder();
		saveResource("items.xml", false);
		saveResource("recipes.xml", false);
		saveResource("factories.xml", false);
		
		SAXBuilder builder = new SAXBuilder();
		try {
			itemsDoc = builder.build(new File(getDataFolder(), "items.xml"));
			console.sendMessage(ChatColor.WHITE+"Loading items...");
			for (Element element : itemsDoc.getRootElement().getChildren()){
				try {
					MechaItem mi = new MechaItem(element);
					items.put(mi.getName(), mi);
					console.sendMessage(ChatColor.WHITE+"  Loaded "+mi.getDisplayName());
				} catch (MechaException e){
					e.printStackTrace();
				}
			}
		} catch (IOException | JDOMException e){
			console.sendMessage(ChatColor.RED+"FAILED to load items!");
			e.printStackTrace();
		}
		
		try {
			recipesDoc = builder.build(new File(getDataFolder(), "recipes.xml"));
			console.sendMessage(ChatColor.WHITE+"Loading recipes...");
			for (Element element : recipesDoc.getRootElement().getChildren()){
				try {
					MechaFactoryRecipe mi = new MechaFactoryRecipe(element);
					recipes.put(mi.getName(), mi);
					console.sendMessage(ChatColor.WHITE+"  Loaded "+mi.getDisplayName());
				} catch (MechaException e){
					e.printStackTrace();
				}
			}
		} catch (IOException | JDOMException e){
			console.sendMessage(ChatColor.RED+"FAILED to load recipes!");
			e.printStackTrace();
		}
		
		try {
			factoriesDoc = builder.build(new File(getDataFolder(), "factories.xml"));
			console.sendMessage(ChatColor.WHITE+"Loading factories...");
			for (Element element : factoriesDoc.getRootElement().getChildren()){
				try {
					MechaFactory mi = new MechaFactory(element);
					console.sendMessage(ChatColor.WHITE+"  Loaded "+ChatColor.translateAlternateColorCodes('&', mi.getDisplayName()));
				} catch (MechaException e){
					e.printStackTrace();
				}
			}
		} catch (IOException | JDOMException e){
			console.sendMessage(ChatColor.RED+"FAILED to load factories!");
			e.printStackTrace();
		}
	}
	
	@Override
	public void onDisable(){
		
	}
	
}
