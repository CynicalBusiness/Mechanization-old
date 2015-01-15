package me.capit.mechanization.factory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.capit.eapi.data.DataModel;
import me.capit.eapi.data.value.StringValue;
import me.capit.eapi.item.ItemHandler;
import me.capit.eapi.math.Vector3;
import me.capit.mechanization.Mechanization;
import me.capit.mechanization.Mechanized;
import me.capit.mechanization.exception.MechaException;
import me.capit.mechanization.recipe.RecipeMatrix;
import me.capit.mechanization.recipe.MechaFactoryRecipe;

public class MechaFactory implements Mechanized, Serializable {
	private static final long serialVersionUID = -6430047152764487993L;
	private final String name, displayName, description;
	private final ChatColor color;
	private final ItemStack activator;
	private final int fuelTime,damage;
	private final boolean captureEvents;
	private final List<MechaFactoryRecipe> recipes = new ArrayList<MechaFactoryRecipe>();
	private final FactoryMatrix matrix;
	
	public MechaFactory(DataModel model) throws MechaException {
		if (!model.getName().equals("factory")) throw new MechaException().new InvalidElementException("factory", model.getName());
		if (model.getAttribute("name")==null) throw new MechaException().new MechaNameNullException();
		name = model.getAttribute("name").getValueString();
		
		try {
			DataModel meta = (DataModel) model.findFirstChild("meta");
			displayName = meta.getAttribute("display").getValueString();
			description = meta.getAttribute("description").getValueString();
			color = ChatColor.valueOf(meta.getAttribute("color").getValueString());
			
			DataModel data = (DataModel) model.findFirstChild("data");
			fuelTime = Integer.parseInt(data.getAttribute("fuel_time").getValueString());
			
			int amount = data.hasAttribute("consume") ? Integer.parseInt(data.getAttribute("consume").getValueString()) : 0;
			int dmg = data.hasAttribute("activator_data") ? Integer.parseInt(data.getAttribute("activator_data").getValueString()) : 0;
			activator = data.getAttribute("activator").getValueString().startsWith("!")
					? ItemHandler.getItem(data.getAttribute("activator").getValueString()).getItemStack(amount, dmg)
					: new ItemStack(Material.valueOf(data.getAttribute("activator").getValueString()), amount, (short) dmg);
					
			damage = Integer.parseInt(data.getAttribute("damage").getValueString());
			captureEvents = Boolean.parseBoolean(data.getAttribute("capture_events").getValueString());
			
			StringValue recs = (StringValue) model.findFirstChild("recipes");
			for (String rec : recs.getValue().trim().split(",")){
				rec=rec.trim();
				if (!Mechanization.recipes.containsKey(rec)){
					Bukkit.getServer().getLogger().info("Failed to find recipe "+rec);
					continue;
				}
				recipes.add(Mechanization.recipes.get(rec));
			}
			
			matrix = new FactoryMatrix((DataModel)  model.findFirstChild("matrix"));
		} catch (NullPointerException | IllegalArgumentException | ClassCastException e){
			e.printStackTrace();
			throw new MechaException().new MechaAttributeInvalidException("Null or invalid tag/attribute value for item "+name+"!");
		}
	}
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getDisplayName() {
		return color+displayName;
	}
	
	public ChatColor getColor(){
		return color;
	}
	
	public int getTimePerFuel(){
		return fuelTime;
	}
	
	public String getDescription(){
		return description;
	}
	
	public ItemStack getActivator(){
		return activator;
	}
	
	public FactoryMatrix getMatrix(){
		return matrix;
	}
	
	public boolean doesCaptureEvents(){
		return captureEvents;
	}
	
	public boolean validActivator(ItemStack activator){
		if ((activator==null && getActivator()!=null) || (activator!=null && getActivator()==null)) return false;
		return RecipeMatrix.materialMatches(activator, getActivator())
				&& RecipeMatrix.metaMatches(activator, getActivator());
	}
	
	public boolean applyActivatorEffects(ItemStack activator){
		if ((activator==null && getActivator()!=null) || (activator!=null && getActivator()==null)) return false;
		if (activator==null && getActivator()==null) return true;
		if (activator.getAmount()==getActivatorConsumption()){
			activator.setType(Material.AIR);
			return true;
		} else if (activator.getAmount()>getActivatorConsumption()) {
			activator.setAmount(activator.getAmount()-getActivatorConsumption());
			activator.setDurability((short) (activator.getDurability() + damage));
			return true;
		}
		return false;
	}
	
	@SuppressWarnings("deprecation")
	public boolean blockMatchesAtLocation(Block b, Vector3 pos){
		if (matrix.getItemStackAtPosition(pos)==null) return true;
		if (matrix.getMaterialAtPosition(pos)!=null && b.getType()!=matrix.getMaterialAtPosition(pos)) return false;
		if (matrix.getDurabilityAtPosition(pos)>-1 && b.getData()!=matrix.getDurabilityAtPosition(pos)) return false;
		return true;
	}
	
	public boolean validForLocation(Location origin, Vector3 relativity){
		for (int x=0; x<matrix.getDims().x; x++){
			for (int y=0; y<matrix.getDims().y; y++){
				for (int z=0; z<matrix.getDims().z; z++){
					Vector3 pos = new Vector3(x,y,z);
					Vector3 relpos = pos.multiply(relativity);
					Block b = origin.getWorld().getBlockAt(
							origin.getBlockX()+(int) relpos.x, 
							origin.getBlockY()+(int) relpos.y, 
							origin.getBlockZ()+(int) relpos.z);
					if (!blockMatchesAtLocation(b,pos)) return false;
				}
			}
		}
		return true;
	}
	
	public List<MechaFactoryRecipe> getRecipes(){
		return recipes;
	}
	
	public MechaFactoryRecipe getRecipeFromInput(Inventory input){
		for (MechaFactoryRecipe recipe : recipes){
			if (recipe.inventoryMatchesInput(input)) return recipe;
		}
		return null;
	}
	
	public List<Vector3> getFurnaceLocations(){
		return matrix.getLocationsOfMaterial(Material.FURNACE, 0);
	}
	
	public int getActivatorDamage(){
		return damage;
	}
	
	public int getActivatorConsumption(){
		return activator.getAmount();
	}
	
	public void setInventoryToOutput(Inventory inv, MechaFactoryRecipe recipe){
		if (inv.getSize()==RecipeMatrix.matrixHeight*RecipeMatrix.matrixWidth) recipe.setInventoryToOutput(inv);
	}
}
