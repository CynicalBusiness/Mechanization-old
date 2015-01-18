package me.capit.mechanization.parser;

import me.capit.eapi.data.Child;
import me.capit.eapi.data.DataModel;
import me.capit.mechanization.Mechanization;
import me.capit.mechanization.factory.MechaFactory;
import me.capit.mechanization.exception.MechaException;
import me.capit.mechanization.recipe.MechaFactoryRecipe;

public class FactoryDataParser {
	
	private int fuel_time, act_consume, act_damage;
	private MaterialParser activator;
	private String[] recipes;
	
	public FactoryDataParser(MechaFactory factory) throws MechaException {
		Child child = factory.getModel().findFirstChild("data");
		if (child==null || !(child instanceof DataModel)) throw new MechaException(factory, "Data model missing or invalid.");
		DataModel data = (DataModel) child;
		
		fuel_time = data.hasAttribute("fuel_time") ? getInt(data.getAttribute("fuel_time").getValueString()) : 1;
		act_consume = data.hasAttribute("consume") ? getInt(data.getAttribute("consume").getValueString()) : 0;
		act_damage = data.hasAttribute("damage") ? getInt(data.getAttribute("damage").getValueString()) : 0;
		
		activator = new MaterialParser(data.hasAttribute("activator") ? data.getAttribute("activator").getValueString() : null);
		if (activator.isWildcard()) Mechanization.warn("Factory "+factory.getName()+" has a wildcard activator.");
		
		recipes = data.hasAttribute("recipes") ? data.getAttribute("recipes").getValueString().split("\\,") : new String[0];
		for (int i=0; i<recipes.length; i++) recipes[i] = recipes[i].trim();
		if (recipes.length==0) Mechanization.warn("Factory "+factory.getName()+" has no registered recipes.");
	}
	
	private int getInt(String dataval){
		try {
			return Integer.parseInt(dataval);
		} catch (IllegalArgumentException e){
			return 0;
		}
	}
	
	public int getFuelTime(){
		return fuel_time;
	}
	public int getActivatorConsumption(){
		return act_consume;
	}
	public int getActivatorDamage(){
		return act_damage;
	}
	
	public MaterialParser getActivator(){
		return activator;
	}
	
	public String[] getRecipeNames(){
		return recipes;
	}
	
	public MechaFactoryRecipe[] getRecipes(){
		MechaFactoryRecipe[] rs = new MechaFactoryRecipe[recipes.length];
		for (int i=0; i<rs.length; i++) rs[i] = Mechanization.recipes.get(recipes[i]);
		return rs;
	}
	
}
