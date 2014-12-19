package me.capit.mechanization.recipe;

import me.capit.mechanization.Mechanization;
import me.capit.mechanization.exception.MechaException;
import me.capit.mechanization.item.MechaItem;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jdom2.Element;

public class RecipeMatrixKey {
	private final char keyChar;
	private final Material material;
	private final int data,amount;
	
	private final MechaItem mechaItem;
	
	public RecipeMatrixKey(Element keyElement) throws MechaException{
		if (!keyElement.getName().equals("key")) throw new MechaException().new InvalidElementException("key", keyElement.getName());
		try {
			if (keyElement.getAttribute("char")!=null) keyChar = keyElement.getAttributeValue("char").charAt(0); else throw null;
			if (keyElement.getAttribute("material")==null){
				mechaItem = null; material = null; data = -1;
			} else if (keyElement.getAttributeValue("material").startsWith("!")){
				mechaItem = Mechanization.items.get(keyElement.getAttributeValue("material").substring(1));
				material = mechaItem!=null ? mechaItem.getBaseMaterial() : Material.AIR;
				data = mechaItem!=null ? mechaItem.getBaseData() : -1;
			} else {
				mechaItem = null;
				material = Material.valueOf(keyElement.getAttributeValue("material"));
				data = keyElement.getAttribute("data")!=null ? Integer.parseInt(keyElement.getAttributeValue("data")) : -1;
			}
			amount = keyElement.getAttribute("amount")!=null ? Integer.parseInt(keyElement.getAttributeValue("amount")) : 1;
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
	public boolean matchesStack(ItemStack is){
		if (material!=null && is.getType()!=material) return false;
		if (data>-1 && is.getDurability()!=data) return false;
		if (material!=Material.AIR && amount>-1 && amount!=is.getAmount()) return false;
		return true;
	}
	
	public ItemStack getItemStack() throws IllegalArgumentException {
		if (mechaItem!=null) return mechaItem.getItemStack(amount);
		if (material==null) throw new IllegalArgumentException();
		ItemStack is = new ItemStack(material, amount);
		if (data>-1) is.setDurability((short) data);
		return is;
	}
}
