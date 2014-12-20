package me.capit.mechanization.factory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jdom2.Element;

import me.capit.mechanization.Mechanization;
import me.capit.mechanization.Mechanized;
import me.capit.mechanization.Position3;
import me.capit.mechanization.exception.MechaException;
import me.capit.mechanization.recipe.RecipeMatrix;
import me.capit.mechanization.recipe.MechaFactoryRecipe;

public class MechaFactory implements Mechanized, Serializable {
	private static final long serialVersionUID = -6430047152764487993L;
	private final String name, displayName, description;
	private final ChatColor color;
	private final ItemStack activator;
	private final int fuelTime,consume,damage;
	private final boolean captureEvents;
	private final List<MechaFactoryRecipe> recipes;
	private final FactoryMatrix matrix;
	
	public MechaFactory(Element element) throws MechaException {
		if (!element.getName().equals("factory")) throw new MechaException().new InvalidElementException("factory", element.getName());
		if (element.getAttribute("name")==null) throw new MechaException().new MechaNameNullException();
		name = element.getAttributeValue("name");
		
		try {
			Element meta = element.getChild("meta");
			if (meta.getAttribute("display")!=null) displayName = meta.getAttributeValue("display"); else throw null;
			if (meta.getAttribute("description")!=null) description = meta.getAttributeValue("description"); else throw null;
			color = meta.getAttribute("color")!=null ? ChatColor.valueOf(meta.getAttributeValue("color")) : ChatColor.WHITE;
			
			Element data = element.getChild("data");
			if (data.getAttribute("fuel_time")!=null) fuelTime = Integer.parseInt(data.getAttributeValue("fuel_time")); else throw null;
			if (data.getAttribute("activator")==null) throw null;
				else if (data.getAttributeValue("activator").startsWith("!")) {
					activator = Mechanization.items.get(data.getAttributeValue("activator").substring(1)).getItemStack();
				} else {
					activator = new ItemStack(Material.valueOf(data.getAttributeValue("activator")),1);
				}
			damage = data.getAttribute("damage")!=null ? Integer.parseInt(data.getAttributeValue("damage")) : 0;
			consume = data.getAttribute("consume")!=null ? Integer.parseInt(data.getAttributeValue("consume")) : 0;
			captureEvents = data.getAttribute("capture_events")!=null ? Boolean.parseBoolean(data.getAttributeValue("capture_events")) : true;
			
			Element recs = element.getChild("recipes");
			recipes = new ArrayList<MechaFactoryRecipe>();
			for (String rec : recs.getValue().split(",")) recipes.add(Mechanization.recipes.get(rec));
			
			matrix = new FactoryMatrix(element.getChild("matrix"));
		} catch (NullPointerException | IllegalArgumentException e){
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
	
	public boolean validActivator(ItemStack is){
		return RecipeMatrix.itemStackMatchesIgnoreQuantity(is, activator);
	}
	
	public boolean applyActivatorEffects(ItemStack is){
		if (is.getAmount()==consume){
			is.setType(Material.AIR);
			return true;
		} else if (is.getAmount()>consume) {
			is.setAmount(is.getAmount()-consume);
			is.setDurability((short) (is.getDurability() - damage));
			return true;
		}
		return false;
	}
	
	@SuppressWarnings("deprecation")
	public boolean blockMatchesAtLocation(Block block, Position3 pos){
		ItemStack is = matrix.getItemStackAtPosition(pos);
		if (is==null) return true;
		if (is.getType()!=block.getType()) return false;
		if (matrix.elementAtPositionRequriesData(pos) && block.getData()!=(byte) is.getDurability()) return false;
		return true;
	}
	
	@SuppressWarnings("deprecation")
	public boolean validForLocation(Location origin, Position3 relativity){
		for (int x=0; x<matrix.getDims().getX(); x++){
			for (int y=0; y<matrix.getDims().getY(); y++){
				for (int z=0; z<matrix.getDims().getZ(); z++){
					Position3 pos = new Position3(x,y,z);
					Position3 relpos = pos.times(relativity);
					Block b = origin.getWorld().getBlockAt(
							origin.getBlockX()+(int) relpos.getX(), 
							origin.getBlockY()+(int) relpos.getY(), 
							origin.getBlockZ()+(int) relpos.getZ());
					if (matrix.getMaterialAtPosition(pos)!=null && b.getType()!=matrix.getMaterialAtPosition(pos)) return false;
					if (matrix.getDurabilityAtPosition(pos)>-1 && b.getData()!=matrix.getDurabilityAtPosition(pos)) return false;
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
	
	public List<Position3> getFurnaceLocations(Position3 relativity){
		return matrix.getLocationsOfMaterial(Material.FURNACE, 0, relativity);
	}
	
	public int getActivatorDamage(){
		return damage;
	}
	
	public int getActivatorConsumption(){
		return consume;
	}
	
	public void setInventoryToOutput(Inventory inv, MechaFactoryRecipe recipe){
		if (inv.getSize()==RecipeMatrix.matrixHeight*RecipeMatrix.matrixWidth) recipe.setInventoryToOutput(inv);
	}
}
