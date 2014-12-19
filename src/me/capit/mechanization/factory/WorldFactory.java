package me.capit.mechanization.factory;

import java.util.ArrayList;
import java.util.List;

import me.capit.mechanization.Mechanization;
import me.capit.mechanization.Position3;
import me.capit.mechanization.exception.MechaException;
import me.capit.mechanization.recipe.RecipeMatrix;
import me.capit.mechanization.recipe.MechaFactoryRecipe;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Chest;
import org.bukkit.block.Furnace;
import org.bukkit.inventory.FurnaceInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class WorldFactory {
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
	
	private final MechaFactory factory;
	private final Position3 origin;
	private final World world;
	private final Chest chest;
	private volatile boolean running = false;
	
	public static WorldFactory getFactoryAtChest(ItemStack activator, Chest chest){
		for (String f : Mechanization.factories.keySet()){
			try {
				return new WorldFactory(f,activator,chest);
			} catch (MechaException e){
				// Do nothing.
			}
		}
		return null;
	}
	
	public WorldFactory(String factory, ItemStack activator, Chest chest) throws MechaException {
		MechaFactory fac = Mechanization.factories.get(factory);
		try {
			if (!(fac.getActivatorDamage()>0
					? RecipeMatrix.itemStackMatchesIgnoreQuantity(activator, fac.getActivator())
					: RecipeMatrix.itemStackMatchesIgnoreQuantityAndDurability(activator, fac.getActivator())))
				throw null;
			this.factory = fac;
			origin = new Position3(chest.getX(),chest.getY(),chest.getZ()).minus(
					fac.getMatrix().getChestLocation().times(getRelativityFrom(chest)));
			world = chest.getWorld();
			this.chest = chest;
		} catch (NullPointerException e){
			throw new MechaException();
		}
	}
	
	// TODO Build this!
	public void activate(){
		new BukkitRunnable(){

			@Override
			public void run() {
				
			}
			
		}.runTaskLater(Mechanization.plugin, 1L);
	}
	
	public List<Furnace> getFurnaces(){
		List<Furnace> furnaces = new ArrayList<Furnace>();
		for (Position3 fur : factory.getFurnaceLocations(getRelativityFrom(chest))){
			fur = fur.plus(origin);
			Furnace f = (Furnace) world.getBlockAt((int) fur.getX(),(int) fur.getY(),(int) fur.getZ());
			furnaces.add(f);
		}
		return furnaces;
	}
	
	public boolean validFurnacesForRecipe(MechaFactoryRecipe recipe, int fuelOffset){
		for (Furnace fur : getFurnaces()){
			Furnace f = (Furnace) world.getBlockAt((int) fur.getX(), (int) fur.getY(), (int) fur.getZ());
			ItemStack fuel = f.getInventory().getFuel(); ItemStack fIn = f.getInventory().getSmelting();
			if (!((fIn==null || fIn.getType()==Material.AIR) && (fuel!=null && 
					fuel.getType()==Material.COAL && fuel.getAmount()>=recipe.getFuel()-fuelOffset))) return false;
		}
		return true;
	}
	
	public boolean validFurnacesForRecipe(MechaFactoryRecipe recipe){
		return validFurnacesForRecipe(recipe,0);
	}
	
	public Location getOriginLocation(){
		return new Location(world,origin.getX(),origin.getY(),origin.getZ());
	}
	
	public boolean valid(Chest chest){
		MechaFactory fac = getFactory();
		return fac!=null && fac.validForLocation(getOriginLocation(), getRelativityFrom(chest));
	}
	
	public MechaFactory getFactory(){
		return factory;
	}
	
	public boolean isRunning(){
		return running;
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
