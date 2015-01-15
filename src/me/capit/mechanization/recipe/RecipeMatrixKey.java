package me.capit.mechanization.recipe;

import me.capit.eapi.data.DataModel;
import me.capit.eapi.item.GameItem;
import me.capit.eapi.item.ItemHandler;
import me.capit.mechanization.exception.MechaException;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class RecipeMatrixKey {
	private final char keyChar;
	private final Material material;
	private final int data,amount;
	
	private final GameItem mechaItem;
	
	public RecipeMatrixKey(DataModel model) throws MechaException{
		if (!model.getName().equals("key")) throw new MechaException().new InvalidElementException("key", model.getName());
		try {
			keyChar = model.getAttribute("char").getValueString().charAt(0);
			if (model.getAttribute("material")==null){
				mechaItem = null; material = null;
			} else if (model.getAttribute("material").getValueString().startsWith("!")){
				mechaItem = ItemHandler.getItem(model.getAttribute("material").getValueString());
				if (mechaItem==null) throw new MechaException("Custom item missing!");
				material = mechaItem.getMaterial();
			} else {
				mechaItem = null;
				material = Material.valueOf(model.getAttribute("material").getValueString());
			}
			data = model.getAttribute("data")!=null ? Integer.parseInt(model.getAttribute("data").getValueString()) : -1;
			amount = model.getAttribute("amount")!=null ? Integer.parseInt(model.getAttribute("amount").getValueString()) : -1;
		} catch (NullPointerException | IllegalArgumentException e){
			throw new MechaException().new MechaAttributeInvalidException("Null or invalid tag/attribute value for key!");
		}
	}
	
	public RecipeMatrixKey(){
		keyChar = ' ';
		material = Material.AIR;
		mechaItem = null;
		amount = -1;
		data = -1;
	}
	
	public char getKeyChar(){
		return keyChar;
	}
	public Material getMaterial(){
		return material;
	}
	public int getData(){
		return data;
	}
	public int getAmount(){
		return amount;
	}
	public boolean matchesStack(ItemStack is) throws IllegalArgumentException {
		return ItemHandler.stackEquals(getItemStack(), is, amount>=0, data>=0);
	}
	
	public ItemStack getItemStack() throws IllegalArgumentException {
		if (mechaItem!=null) return mechaItem.getItemStack(amount, data);
		if (material==null) throw new IllegalArgumentException();
		ItemStack is = new ItemStack(material, amount);
		if (data>-1) is.setDurability((short) data);
		return is;
	}
}
