package me.capit.mechanization.recipe;

import me.capit.eapi.data.Child;
import me.capit.eapi.data.DataModel;
import me.capit.mechanization.Mechanization;
import me.capit.mechanization.parser.MaterialParser;


public class RecipeMatrixKey {
	private final char keyChar;
	private final MaterialParser material;
	private int amount;
	
	public RecipeMatrixKey(Child child) throws IllegalArgumentException {
		if (child==null || !(child instanceof DataModel) || !child.getName().equals("key"))
			throw new IllegalArgumentException("Key was missing, not a model, or not a key.");
		DataModel model = (DataModel) child;
		
		try {
			keyChar = model.getAttribute("char").getValueString().charAt(0);
		} catch (IndexOutOfBoundsException | NullPointerException e){
			throw new IllegalArgumentException("Recipe key must bind to a char.");
		}
		if (keyChar==',' || keyChar==' ') throw new IllegalArgumentException("Keys cannot be bound to a comma or a space.");
		
		try {
			amount = Integer.parseInt(model.getAttribute("amount").getValueString());
		} catch (NullPointerException | IllegalArgumentException e){
			Mechanization.warn("Amount attribute missing for key: 1 is assumed.");
			amount = 1;
		}
		
		material = new MaterialParser(model.hasAttribute("material") ? model.getAttribute("material").getValueString() : null);
		if (material.isWildcard()) Mechanization.warn("A recipe key has a wildcard material. Is it valid?");
	}
	
	public RecipeMatrixKey(){
		keyChar = 0;
		material = new MaterialParser("AIR");
		amount = 1;
	}
	
	public char getKeyChar(){
		return keyChar;
	}
	
	public MaterialParser getMaterial(){
		return material;
	}
	
	public int getAmount(){
		return amount;
	}
}
