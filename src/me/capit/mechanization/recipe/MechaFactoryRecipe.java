package me.capit.mechanization.recipe;

import java.io.Serializable;

import org.bukkit.ChatColor;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.capit.eapi.data.Child;
import me.capit.eapi.data.DataModel;
import me.capit.mechanization.Mechanized;
import me.capit.mechanization.exception.MechaException;

public class MechaFactoryRecipe implements Mechanized, Serializable {
	private static final long serialVersionUID = 7377065024361610946L;
	private final String name,displayName,description;
	private int fuel;
	private RecipeMatrix input,output;
	private DataModel keys;
	
	public MechaFactoryRecipe(DataModel recipe) throws MechaException {
		if (!recipe.getName().equals("recipe")) throw new MechaException().new InvalidElementException("recipe", recipe.getName());
		if (recipe.getAttribute("name")==null) throw new MechaException().new MechaNameNullException();
		name = recipe.getAttribute("name").getValueString();
		try {
			DataModel meta = (DataModel) recipe.findFirstChild("meta");
			displayName = meta.getAttribute("display_name").getValueString();
			description = meta.getAttribute("description").getValueString();
			fuel = Integer.parseInt(meta.getAttribute("fuel_cost").getValueString());
			
			keys = (DataModel) recipe.findFirstChild("keys");
			
			DataModel shape = (DataModel) recipe.findFirstChild("shape");
			input = new RecipeMatrix(shape.getAttribute("input").getValueString());
			output = new RecipeMatrix(shape.getAttribute("output").getValueString());
		} catch (NullPointerException | IllegalArgumentException | ClassCastException e){
			e.printStackTrace();
			throw new MechaException().new MechaAttributeInvalidException("Null or invalid tag/attribute value for "+name+"!");
		}
	}
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getDisplayName() {
		return ChatColor.translateAlternateColorCodes('&', displayName);
	}
	
	public String getDescription() {
		return ChatColor.translateAlternateColorCodes('&', description);
	}
	
	public DataModel getKeys(){
		return keys;
	}
	
	public ItemStack getItemStackByKey(RecipeMatrixKey key) throws IllegalArgumentException {
		return key.getItemStack();
	}
	
	public RecipeMatrixKey getKeyByKeyChar(char keyChar){
		for (Child ke : getKeys().getChildren()){
			try {
				RecipeMatrixKey key = new RecipeMatrixKey((DataModel) ke);
				if (key.getKeyChar()==keyChar) return key;
			} catch (MechaException | ClassCastException e){
				// Do nothing.
			}
		}
		return new RecipeMatrixKey();
	}
	
	public RecipeMatrixKey getKeyAtInputSlot(int slot){
		return getKeyByKeyChar(input.getCharAtSlot(slot));
	}
	
	public RecipeMatrixKey getKeyAtOutputSlot(int slot){
		return getKeyByKeyChar(output.getCharAtSlot(slot));
	}
	
	public boolean inventoryMatchesInput(Inventory inv){
		for (int i = 0; i<27; i++){
			if (!getKeyAtInputSlot(i).matchesStack(inv.getItem(i))) return false;
		}
		return true;
	}
	
	public void setInventoryToOutput(Inventory inv){
		inv.clear();
		for (int i=0; i<27; i++){
			inv.setItem(i,getItemStackByKey(getKeyAtOutputSlot(i)));
		}
	}
	
	public RecipeMatrix getInput(){
		return input;
	}
	
	public RecipeMatrix getOutput(){
		return output;
	}
	
	public int getFuel(){
		return fuel;
	}

}
