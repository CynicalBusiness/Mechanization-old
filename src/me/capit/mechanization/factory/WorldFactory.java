package me.capit.mechanization.factory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import me.capit.mechanization.Mechanization;
import me.capit.mechanization.Position3;
import me.capit.mechanization.recipe.RecipeMatrix;
import me.capit.mechanization.recipe.MechaFactoryRecipe;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Chest;
import org.bukkit.block.Furnace;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.FurnaceInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class WorldFactory implements ConfigurationSerializable {
	public static boolean takeFurnaceFuel(Furnace f){
		return takeFurnaceFuel(f,1);
	}
	
	public static boolean takeFurnaceFuel(Furnace f, int fuel){
		FurnaceInventory inv = f.getInventory();
		if (inv.getFuel().getAmount()>fuel){
			ItemStack is = inv.getFuel();
			is.setAmount(is.getAmount()-fuel);
			return true;
		} else if (inv.getFuel().getAmount()==fuel){
			inv.setFuel(new ItemStack(Material.AIR));
			return true;
		}
		return false;
	}
	
	public final String factoryName;
	private Position3 origin;
	private World world;
	private volatile boolean running = false;
	
	public WorldFactory(Map<String, Object> data){
		factoryName = (String) data.get("FACTORY");
		origin = (Position3) data.get("POSITION");
		world = Bukkit.getWorld((String) data.get("WORLD"));
	}
	
	// TODO Gotta make sure this works!
	public boolean activate(ItemStack activator, final Chest chest){
		if (!valid(chest)) return false;
		final MechaFactory fac = Mechanization.factories.get(factoryName);
		if (fac.getActivator()!=null && !RecipeMatrix.itemStackMatchesIgnoreQuantity(activator, fac.getActivator())) return false;
		final MechaFactoryRecipe recipe = fac.getRecipeFromInput(chest.getBlockInventory());
		if (recipe==null || !validFurnaces(fac, recipe, chest)) return false;
		final List<Furnace> furnaces = getFurnaces(fac, chest);
		
		new BukkitRunnable(){

			@Override
			public void run() {
				int fuel = recipe.getFuel();
				int fuelOff = fuel;
				MechaFactoryRecipe rec = recipe;
				while (rec!=null && valid(chest) && validFurnaces(fac, recipe, chest, fuelOff-fuel) && fuel>0){
					int time = fac.getTimePerFuel();
					for (Furnace f : furnaces){
						f.setBurnTime((short) time);
						takeFurnaceFuel(f);
					}
					try {
						TimeUnit.SECONDS.sleep((long) time);
					} catch (InterruptedException e) {
						e.printStackTrace();
						break;
					}
					rec = fac.getRecipeFromInput(chest.getBlockInventory());
					fuel--;
				}
				if (fuel==0 && rec!=null && valid(chest)){
					fac.setInventoryToOutput(chest.getBlockInventory(), rec);
					world.playSound(chest.getLocation(),Sound.LEVEL_UP, 1, 1);
				}
			}
			
		}.runTaskLater(Mechanization.plugin, 1L);
		
		
		return true;
	}
	
	public List<Furnace> getFurnaces(MechaFactory fac, Chest chest){
		List<Furnace> furnaces = new ArrayList<Furnace>();
		for (Position3 fur : fac.getFurnaceLocations(getRelativityFrom(chest))){
			fur = fur.plus(origin);
			Furnace f = (Furnace) world.getBlockAt((int) fur.getX(),(int) fur.getY(),(int) fur.getZ());
			furnaces.add(f);
		}
		return furnaces;
	}
	
	public boolean validFurnaces(MechaFactory fac, MechaFactoryRecipe recipe, Chest chest, int fuelOffset){
		for (Furnace fur : getFurnaces(fac,chest)){
			Furnace f = (Furnace) world.getBlockAt((int) fur.getX(), (int) fur.getY(), (int) fur.getZ());
			ItemStack fuel = f.getInventory().getFuel(); ItemStack fIn = f.getInventory().getSmelting();
			if (!((fIn==null || fIn.getType()==Material.AIR) && (fuel!=null && 
					fuel.getType()==Material.COAL && fuel.getAmount()>=recipe.getFuel()-fuelOffset))) return false;
		}
		return true;
	}
	
	public boolean validFurnaces(MechaFactory fac, MechaFactoryRecipe recipe, Chest chest){
		return validFurnaces(fac, recipe, chest, 0);
	}
	
	public Location getOriginLocation(){
		return new Location(world,origin.getX(),origin.getY(),origin.getZ());
	}
	
	public boolean valid(Chest chest){
		MechaFactory fac = getFactory();
		return fac!=null && fac.validForLocation(getOriginLocation(), getRelativityFrom(chest));
	}
	
	public MechaFactory getFactory(){
		return Mechanization.factories.get(factoryName);
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
	
	public Position3 getRelativityFrom(Chest chest){
		Position3 rel = new Position3(0,1,0);
		switch(((org.bukkit.material.Chest) chest.getBlock()).getFacing()){
		case NORTH:
			rel.setX(-1);
			break;
		case SOUTH:
			rel.setX(1);
			break;
		case WEST:
			rel.setZ(-1);
			break;
		case EAST:
			rel.setZ(1);
			break;
		default:
			break;
		}
		return rel;
	}

}
