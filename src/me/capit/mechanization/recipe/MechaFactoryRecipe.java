package me.capit.mechanization.recipe;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import me.capit.mechanization.Mechanization;
import me.capit.mechanization.Mechanized;

public class MechaFactoryRecipe implements Mechanized, Serializable {
	private static final long serialVersionUID = 7377065024361610946L;
	private final String name;
	private JSONParser p = new JSONParser();
	private JSONObject json;

	private FactoryRecipeMatrix input; private FactoryRecipeMatrix output;
	
	public MechaFactoryRecipe(File file){
		name = file.getName().replaceFirst("[.][^.]+$", "");
		try {
			FileReader reader = new FileReader(file);
			json = (JSONObject) p.parse(reader);
			input = new FactoryRecipeMatrix((JSONArray) json.get("input"));
			output = new FactoryRecipeMatrix((JSONArray) json.get("output"));
		} catch (IOException | ParseException | NullPointerException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getDisplayName() {
		return (String) json.get("display_name");
	}

	@Override
	public JSONObject getJSON() {
		return json;
	}
	
	public ItemStack getItemStackByKey(char key){
		if (key==' ') return new ItemStack(Material.AIR);
		JSONObject keys = (JSONObject) json.get("keys");
		if (keys.get(String.valueOf(key))!=null){
			JSONArray data = (JSONArray) keys.get(String.valueOf(key));
			for (Object o : data){
				String s = (String) o;
				String[] isdata = s.split(":");
				if (isdata[0].startsWith("!")){
					if (Mechanization.items.containsKey(isdata[0].substring(1))){
						return Mechanization.items.get(isdata[0].substring(1)).getItemStack(Integer.parseInt(isdata[2]));
					} else {
						Mechanization.logger.warning("Attempted to compare bad custom item '"+isdata[0].substring(1)+"'!");
					}
				} else {
					ItemStack is = new ItemStack(Material.valueOf(isdata[0]), Integer.parseInt(isdata[2]));
					is.setDurability(Short.parseShort(isdata[1]));
					return is;
				}
			}
		}
		return null;
	}
	
	public boolean inputMatches(Inventory inv){
		for (int i=0; i<27; i++){
			if (!FactoryRecipeMatrix.itemStackMatches(getItemStackByKey(input.getKeyAtSlot(i)), inv.getItem(i))) return false;
		}
		return true;
	}
	
	public void setInventoryToOutput(Inventory inv){
		inv.clear();
		for (int i=0; i<27; i++){
			inv.setItem(i,getItemStackByKey(output.getKeyAtSlot(i)));
		}
	}
	
	public FactoryRecipeMatrix getInput(){
		return input;
	}
	
	public FactoryRecipeMatrix getOutput(){
		return output;
	}
	
	public int getFuel(){
		return (int) json.get("fuel");
	}

}
