package me.capit.mechanization;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Logger;

import me.capit.eapi.DataHandler;
import me.capit.eapi.data.Child;
import me.capit.eapi.data.DataFile;
import me.capit.eapi.data.DataModel;
import me.capit.eapi.data.value.StringValue;
import me.capit.eapi.item.GameItem;
import me.capit.eapi.item.ItemHandler;
import me.capit.mechanization.exception.MechaException;
import me.capit.mechanization.factory.MechaFactory;
import me.capit.mechanization.recipe.MechaFactoryRecipe;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.jdom2.JDOMException;

public class Mechanization extends JavaPlugin {
	public static final HashMap<String, MechaFactoryRecipe> recipes = new HashMap<String, MechaFactoryRecipe>();
	public static final HashMap<String, MechaFactory> factories = new HashMap<String, MechaFactory>();
	public static Logger logger; public static ConsoleCommandSender console;
	public static File pluginDir;
	
	public static DataFile factoriesFile,itemsFile,recipesFile;
	
	public static Mechanization plugin;
	
	@Override
	public void onEnable(){
		logger = getLogger();
		console = getServer().getConsoleSender();
		plugin = this;
		
		console.sendMessage(ChatColor.WHITE+"---- "+ChatColor.YELLOW+"Mechanization"+ChatColor.WHITE+" -------------------");
		
		console.sendMessage(ChatColor.WHITE+"Initializing directories and loading defaults...");
		pluginDir = getDataFolder();
		saveDefaultResource("items.xml");
		saveDefaultResource("recipes.xml");
		saveDefaultResource("factories.xml");
		
		this.getServer().getPluginManager().registerEvents(new MechaDataController(this), this);
		
		
		try {
			itemsFile = DataHandler.loadXMLFile(this, "items");
			console.sendMessage(ChatColor.WHITE+"Loading items...");
			for (Child child : itemsFile.getChildren()){
				if (child instanceof DataModel){
					try {
						DataModel model = (DataModel) child;
						String name = model.getAttribute("name").getValueString();
						String mat = model.getAttribute("material").getValueString().toUpperCase();
						StringValue display = (StringValue) model.findFirstChild("display");
						StringValue lore = (StringValue) model.findFirstChild("lore");
						GameItem item = new GameItem(name, Material.valueOf(mat));
						item.setDisplayName(ChatColor.translateAlternateColorCodes('&', display.getValue()));
						item.setLore(lore.getValue().split("\\|"));
						ItemHandler.registerItem(item);
						console.sendMessage(ChatColor.WHITE+"  Loaded "+item.getDisplayName());
					} catch (IllegalArgumentException | NullPointerException | ClassCastException e){
						console.sendMessage(ChatColor.RED+"  ERROR: "+ChatColor.WHITE+" Bad data member "+child.getName());
					}
				} else {
					console.sendMessage(ChatColor.RED+"  ERROR: "+ChatColor.WHITE+" Unknown member "+child.getName());
				}
			}
		} catch (IOException | JDOMException e){
			console.sendMessage(ChatColor.RED+"FAILED to load items!");
			e.printStackTrace();
		}
		
		try {
			recipesFile = DataHandler.loadXMLFile(this, "recipes");
			console.sendMessage(ChatColor.WHITE+"Loading recipes...");
			for (Child element : recipesFile.getChildren()){
				try {
					MechaFactoryRecipe mi = new MechaFactoryRecipe((DataModel) element);
					recipes.put(mi.getName(), mi);
					console.sendMessage(ChatColor.WHITE+"  Loaded "+mi.getDisplayName());
				} catch (MechaException | ClassCastException e){
					e.printStackTrace();
				}
			}
		} catch (IOException | JDOMException e){
			console.sendMessage(ChatColor.RED+"FAILED to load recipes!");
			e.printStackTrace();
		}
		
		try {
			factoriesFile = DataHandler.loadXMLFile(this, "factories");
			console.sendMessage(ChatColor.WHITE+"Loading factories...");
			for (Child element : factoriesFile.getChildren()){
				try {
					MechaFactory mi = new MechaFactory((DataModel) element);
					factories.put(mi.getName(), mi);
					console.sendMessage(ChatColor.WHITE+"  Loaded "+ChatColor.translateAlternateColorCodes('&', mi.getDisplayName()));
				} catch (MechaException | ClassCastException e){
					e.printStackTrace();
				}
			}
		} catch (IOException | JDOMException e){
			console.sendMessage(ChatColor.RED+"FAILED to load factories!");
			e.printStackTrace();
		}
	}
	
	public void saveDefaultResource(String resource){
		if (!new File(getDataFolder(), resource).exists()) saveResource(resource,false);
	}
	
}
