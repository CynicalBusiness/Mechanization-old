package me.capit.mechanization.recipe;

import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONArray;

public class FactoryRecipeMatrix {
	public static boolean itemStackMatches(ItemStack one, ItemStack two){
		return one.getAmount()==two.getAmount() && one.getType()==two.getType() && 
				one.getItemMeta().getDisplayName().equals(two.getItemMeta().getDisplayName());
	}
	
	private JSONArray matrix;
	
	public FactoryRecipeMatrix(JSONArray matrix){
		this.matrix=matrix;
	}
	
	public char getKeyAtSlot(int slot){
		return ((String) matrix.get((slot+1)/9)).charAt(slot%9);
	}
}
