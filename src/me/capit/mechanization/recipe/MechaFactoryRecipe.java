package me.capit.mechanization.recipe;

import java.io.Serializable;

import org.bukkit.ChatColor;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jdom2.Element;

import me.capit.mechanization.Mechanized;
import me.capit.mechanization.exception.MechaException;

public class MechaFactoryRecipe implements Mechanized, Serializable {
	private static final long serialVersionUID = 7377065024361610946L;
	private final String name,displayName,description;
	private int fuel;
	private RecipeMatrix input,output;
	private Element keys;
	
	public MechaFactoryRecipe(Element element) throws MechaException {
		if (!element.getName().equals("recipe")) throw new MechaException().new InvalidElementException("recipe", element.getName());
		if (element.getAttribute("name")==null) throw new MechaException().new MechaNameNullException();
		name = element.getAttributeValue("name");
		try {
			Element meta = element.getChild("meta");
			if (meta.getAttribute("display")!=null) displayName = meta.getAttributeValue("display"); else throw null;
			if (meta.getAttribute("description")!=null) description = meta.getAttributeValue("description"); else throw null;
			if (meta.getAttribute("fuel_cost")!=null) fuel = Integer.parseInt(meta.getAttributeValue("fuel_cost")); else throw null;
			
			if (element.getChild("keys")!=null) keys = element.getChild("keys"); else throw null;
			
			input = new RecipeMatrix(element.getChild("input").getAttributeValue("matrix"));
			output = new RecipeMatrix(element.getChild("output").getAttributeValue("matrix"));
		} catch (NullPointerException | IllegalArgumentException e){
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
	
	public Element getKeys(){
		return keys;
	}
	
	public ItemStack getItemStackByKey(RecipeMatrixKey key) throws IllegalArgumentException {
		return key.getItemStack();
	}
	
	public RecipeMatrixKey getKeyByKeyChar(char keyChar){
		for (Element ke : getKeys().getChildren()){
			try {
				RecipeMatrixKey key = new RecipeMatrixKey(ke);
				if (key.getKeyChar()==keyChar) return key;
			} catch (MechaException e){
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
