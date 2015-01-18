package me.capit.mechanization.parser;

import me.capit.eapi.data.Child;
import me.capit.eapi.data.DataModel;
import me.capit.mechanization.Mechanization;
import me.capit.mechanization.exception.MechaException;
import me.capit.mechanization.recipe.MechaFactoryRecipe;
import me.capit.mechanization.recipe.RecipeMatrixKey;
import me.capit.mechanization.recipe.RecipeMatrix;

public class FactoryRecipeParser {

	private RecipeMatrixKey[] keys;
	private RecipeMatrix input, output;
	private int fuel_cost;
	
	public FactoryRecipeParser(MechaFactoryRecipe recipe) throws MechaException {
		Child shapechild = recipe.getModel().findFirstChild("shape");
		if (shapechild==null || !(shapechild instanceof DataModel)) throw new MechaException(recipe, "Shape data missing or invalid.");
		DataModel matrixmodel = (DataModel) shapechild;
		
		try {
			fuel_cost = Integer.parseInt(matrixmodel.getAttribute("fuel_cost").getValueString());
		} catch (IllegalArgumentException | NullPointerException e){
			fuel_cost = 1;
		}
		
		try {
			input = new RecipeMatrix(matrixmodel.hasAttribute("input") ? matrixmodel.getAttribute("input").getValueString() : null);
			output = new RecipeMatrix(matrixmodel.hasAttribute("output") ? matrixmodel.getAttribute("output").getValueString() : null);
		} catch (IllegalArgumentException e){
			throw new MechaException(recipe,e.getMessage());
		}
		
		
		Child keychild = recipe.getModel().findFirstChild("keys");
		if (keychild==null || !(keychild instanceof DataModel)) throw new MechaException(recipe, "Key data missing or invalid.");
		DataModel keymodel = (DataModel) keychild;
		
		keys = new RecipeMatrixKey[keymodel.getChildren().size()];
		for (int i=0; i<keys.length; i++){
			try {
				keys[i] = new RecipeMatrixKey(keymodel.getChildren().get(i));
			} catch (IllegalArgumentException e){
				Mechanization.warn(recipe.getName()+": "+e.getMessage());
			}
		}
	}
	
	public RecipeMatrixKey[] getKeys(){
		return keys;
	}
	
	public RecipeMatrixKey getKey(char keyChar){
		for (RecipeMatrixKey key : getKeys()) if (key.getKeyChar()==keyChar) return key;
		return new RecipeMatrixKey();
	}
	
	public RecipeMatrix getInput(){
		return input;
	}
	
	public RecipeMatrix getOutput(){
		return output;
	}
	
	public int getFuelCost(){
		return fuel_cost;
	}
}
