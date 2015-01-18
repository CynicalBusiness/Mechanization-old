package me.capit.mechanization.recipe;

import java.io.Serializable;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.capit.eapi.data.DataModel;
import me.capit.mechanization.Mechanized;
import me.capit.mechanization.exception.MechaException;
import me.capit.mechanization.parser.MetaParser;
import me.capit.mechanization.parser.FactoryRecipeParser;

public class MechaFactoryRecipe implements Mechanized, Serializable {
	private static final long serialVersionUID = 7377065024361610946L;
	private final String name;
	private final MetaParser meta;
	private final FactoryRecipeParser recipe;
	private DataModel model;
	
	public MechaFactoryRecipe(DataModel model) throws MechaException {
		if (!model.getName().equals("recipe")) throw new MechaException(this, "Model name is not 'recipe'!");
		if (!model.hasAttribute("name")) throw new MechaException(this, "Name attribute is missing.");
		name = model.getAttribute("name").getValueString();
		this.model = model;
		
		meta = new MetaParser(this);
		recipe = new FactoryRecipeParser(this);
	}
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public DataModel getModel(){
		return model;
	}
	
	@Override
	public MetaParser getMeta(){
		return meta;
	}
	
	public FactoryRecipeParser getRecipe(){
		return recipe;
	}
	
	public boolean inventoryMatchesInput(Inventory inv){
		for (int i = 0; i<RecipeMatrix.matrixSize; i++){
			ItemStack is = inv.getItem(i);
			RecipeMatrixKey key = getRecipe().getKey(getRecipe().getInput().getCharAtSlot(i));
			if (!key.getMaterial().isInput(is, false)) return false;
		}
		return true;
	}
	
	public void updateInventoryToOutput(Inventory inv){
		inv.clear();
		for (int i=0; i<inv.getSize(); i++){
			RecipeMatrixKey key = getRecipe().getKey(getRecipe().getOutput().getCharAtSlot(i));
			inv.setItem(i, key.getMaterial().getOutput(key.getAmount()));
		}
	}
}
