package me.capit.mechanization.recipe;

public class RecipeMatrix {
	public static final int matrixHeight = 3;
	public static final int matrixWidth = 9;
	public static final int matrixSize = matrixHeight*matrixWidth;
	
	public static boolean matrixValid(String[] matrix){
		if (matrix.length!=matrixHeight) return false;
		for (String row : matrix){
			if (row.length()!=matrixWidth) return false;
		}
		return true;
	}
	
	private final String[] matrix;
	
	public RecipeMatrix(String matrix) throws IllegalArgumentException {
		if (matrix==null) throw new IllegalArgumentException("The matrix cannot be null.");
		this.matrix=matrix.split(",");
		if (!matrixValid(this.matrix)) throw new IllegalArgumentException("The matrix must be "+matrixWidth+"x"+matrixHeight+".");
	}
	
	public char getCharAtPos(int row, int col) throws ArrayIndexOutOfBoundsException {
		String r = matrix[row];
		return r.charAt(col);
	}
	
	public char getCharAtSlot(int slot) throws IndexOutOfBoundsException {
		if (slot>matrixSize) throw new IndexOutOfBoundsException();
		int row = 0;
		while (slot>=matrixWidth){row++; slot-=matrixWidth;}
		return getCharAtPos(row, slot);
	}
}
