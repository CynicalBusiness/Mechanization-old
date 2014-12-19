package me.capit.mechanization.recipe;

import me.capit.mechanization.exception.MechaException;

import org.bukkit.inventory.ItemStack;

public class RecipeMatrix {
	public static final int matrixHeight = 3;
	public static final int matrixWidth = 9;
	
	public static boolean itemStackMatchesIgnoreMeta(ItemStack one, ItemStack two){
		return one.getAmount()==two.getAmount() && one.getType()==two.getType() && 
				one.getDurability() == one.getDurability();
	}
	
	public static boolean itemStackMatchesIgnoreDurability(ItemStack one, ItemStack two){
		return one.getAmount()==two.getAmount() && one.getType()==two.getType() && 
				one.getItemMeta().getDisplayName().equals(two.getItemMeta().getDisplayName());
	}
	
	public static boolean itemStackMatchesIgnoreQuantity(ItemStack one, ItemStack two){
		return one.getType()==two.getType() && one.getItemMeta().getDisplayName().equals(two.getItemMeta().getDisplayName()) && 
				one.getDurability() == one.getDurability();
	}
	
	public static boolean itemStackMatches(ItemStack one, ItemStack two){
		return one.getAmount()==two.getAmount() && one.getType()==two.getType() && 
				one.getItemMeta().getDisplayName().equals(two.getItemMeta().getDisplayName()) && 
				one.getDurability() == one.getDurability();
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
		if (!matrixValid(getMatrixAsArray())) throw new MechaException().new MechaAttributeInvalidException("Input string not a valid matrix.");
		this.matrix=matrix;
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
		int row = 0, col = 0;
		while (col>=matrixWidth){row++; col-=matrixWidth;}
		return getCharAtPos(row, col);
	}
	
	public String[] getMatrixAsArray(){
		return matrix.split(",");
	}
	
	public String getMatrixString(){
		return matrix;
	}
}
