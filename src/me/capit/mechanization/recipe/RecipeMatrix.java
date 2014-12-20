package me.capit.mechanization.recipe;

import me.capit.mechanization.exception.MechaException;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class RecipeMatrix {
	public static final int matrixHeight = 3;
	public static final int matrixWidth = 9;
	
	public static boolean quantityMatches(ItemStack one, ItemStack two){
		return one.getAmount()==two.getAmount();
	}
	
	public static boolean durabilityMatches(ItemStack one, ItemStack two){
		return one.getDurability()==two.getDurability();
	}
	
	public static boolean materialMatches(ItemStack one, ItemStack two){
		return one.getType()==two.getType();
	}
	
	public static boolean metaMatches(ItemStack one, ItemStack two){
		ItemMeta oneMeta = one.getItemMeta();
		ItemMeta twoMeta = two.getItemMeta();
		if ((oneMeta.hasDisplayName() && !twoMeta.hasDisplayName()) || (!oneMeta.hasDisplayName() && twoMeta.hasDisplayName())) return false;
		if ((oneMeta.hasLore() && !twoMeta.hasLore()) || (!oneMeta.hasLore() && twoMeta.hasLore())) return false;
		if (oneMeta.hasDisplayName() && twoMeta.hasDisplayName() && !oneMeta.getDisplayName().equals(twoMeta.getDisplayName())) return false;
		if (oneMeta.hasLore() && twoMeta.hasLore() && !oneMeta.getLore().equals(twoMeta.getLore())) return false;
		return true;
	}
	
	public static boolean matrixValid(String[] matrix){
		if (matrix.length!=matrixHeight) return false;
		for (String row : matrix){
			if (row.length()!=matrixWidth) return false;
		}
		return true;
	}
	
	private final String matrix;
	
	public RecipeMatrix(String matrix) throws NullPointerException,MechaException {
		if (matrix==null) throw null;
		this.matrix=matrix;
		if (!matrixValid(getMatrixAsArray())) throw new MechaException().new MechaAttributeInvalidException("Input string not a valid matrix.");
	}
	
	public int getMatrixSlotLength(){
		return matrix.replaceAll(",", "").length();
	}
	
	public char getCharAtPos(int row, int col) throws IndexOutOfBoundsException {
		String[] ma = getMatrixAsArray();
		String r = ma[row];
		return r.charAt(col);
	}
	
	public char getCharAtSlot(int slot) throws IndexOutOfBoundsException {
		if (slot>getMatrixSlotLength()) throw new IndexOutOfBoundsException();
		int row = 0;
		while (slot>=matrixWidth){row++; slot-=matrixWidth;}
		return getCharAtPos(row, slot);
	}
	
	public String[] getMatrixAsArray(){
		return matrix.split(",");
	}
	
	public String getMatrixString(){
		return matrix;
	}
}
