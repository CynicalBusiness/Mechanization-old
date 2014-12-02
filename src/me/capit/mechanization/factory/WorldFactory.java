package me.capit.mechanization.factory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.capit.mechanization.Mechanization;
import me.capit.mechanization.Position3;
import me.capit.mechanization.recipe.FactoryRecipeMatrix;
import me.capit.mechanization.recipe.MechaFactoryRecipe;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Chest;
import org.bukkit.block.Furnace;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.inventory.ItemStack;

public class WorldFactory implements ConfigurationSerializable {
	{ConfigurationSerialization.registerClass(WorldFactory.class);}
	
	public final String factoryName; private Position3 origin; private World world;
	private boolean valid = false; private volatile boolean running = false;
	
	public WorldFactory(Map<String, Object> data){
		factoryName = (String) data.get("FACTORY");
		origin = (Position3) data.get("POSITION");
		world = Bukkit.getWorld((String) data.get("WORLD"));
	}
	
	public boolean activate(ItemStack activator, Chest chest){
		validate(chest);
		if (!valid) return false;
		MechaFactory fac = Mechanization.factories.get(factoryName);
		if (fac.getActivatorStack()!=null && !FactoryRecipeMatrix.itemStackMatchesIgnoreQuantity(activator, fac.getActivatorStack())) return false;
		MechaFactoryRecipe recipe = fac.getRecipeFromInput(chest.getBlockInventory());
		if (recipe==null) return false;
		List<Furnace> furnaces = new ArrayList<Furnace>();
		for (Position3 pos : fac.getFurnaceLocations()){
			Position3 fur = pos.times(fac.getRelativityFrom(chest)).plus(origin);
			Furnace f = (Furnace) world.getBlockAt((int) fur.getX(), (int) fur.getY(), (int) fur.getZ());
			ItemStack fuel = f.getInventory().getFuel(); ItemStack fIn = f.getInventory().getSmelting();
			if (!((fIn==null || fIn.getType()==Material.AIR) && (fuel!=null && 
					fuel.getType()==Material.COAL && fuel.getAmount()>=recipe.getFuel()))) return false;
			furnaces.add(f);
		}
		// TODO
		
		return true;
	}
	
	public void validate(Chest chest){
		valid = Mechanization.factories.containsKey(factoryName) && 
				Mechanization.factories.get(factoryName).validAtChestLocation(chest.getLocation());
	}
	
	public boolean isValidated(){
		return valid;
	}
	
	public boolean isRunning(){
		return running;
	}
	
	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("FACTORY", factoryName);
		data.put("POSITION", origin);
		data.put("WORLD", world.getName());
		return data;
	}

}
