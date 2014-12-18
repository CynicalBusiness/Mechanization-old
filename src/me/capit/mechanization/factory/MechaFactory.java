package me.capit.mechanization.factory;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import me.capit.mechanization.Mechanization;
import me.capit.mechanization.Mechanized;
import me.capit.mechanization.Position3;
import me.capit.mechanization.recipe.MechaFactoryRecipe;

public class MechaFactory implements Mechanized, Serializable {
	private static final long serialVersionUID = -6430047152764487993L;
	private final String name; private JSONObject json; private Position3 chestLoc;
	private JSONParser p = new JSONParser();
	
	public MechaFactory(File file){
		name = file.getName().replaceFirst("[.][^.]+$", "");
		try {
			FileReader reader = new FileReader(file);
			json = (JSONObject) p.parse(reader);
			chestLoc = Position3.fromList((JSONArray) json.get("chest"));
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
	
	public int getTimePerFuel(){
		return (int) json.get("time_per_fuel");
	}
	
	public boolean formatMatchesDims(){
		JSONArray format = (JSONArray) json.get("format");
		if (format.size()!=(int) json.get("height")) return false;
		for (Object o1 : format){
			JSONArray arr = (JSONArray) o1;
			if (arr.size()!=(int) json.get("depth")) return false;
			for (Object o2 : arr){
				String str = (String) o2;
				if (str.length()!=(int) json.get("width")) return false;
			}
		}
		JSONArray first = (JSONArray) format.get((int) chestLoc.getY());
		String line = (String) first.get((int) chestLoc.getZ());
		if (line.charAt((int) chestLoc.getX())!='@') return false;
		return true;
	}
	
	@SuppressWarnings("deprecation")
	public boolean blockMatchesKey(char key, Block block){
		JSONObject keys = (JSONObject) json.get("keys");
		JSONArray blocks = (JSONArray) keys.get(String.valueOf(key));
		for (Object o : blocks){
			String data = (String) o;
			if (block.getType()==Material.valueOf(data.split(":")[1]) && block.getData()==Byte.parseByte(data.split(":")[1])) return true;
		}
		return false;
	}
	
	public boolean materialMatchesKey(char key, Material mat){
		JSONObject keys = (JSONObject) json.get("keys");
		JSONArray blocks = (JSONArray) keys.get(String.valueOf(key));
		for (Object o : blocks){
			String data = (String) o;
			if (mat==Material.valueOf(data.split(":")[1])) return true;
		}
		return false;
	}
	
	public char getKeyFromMaterial(Material mat){
		JSONObject keys = (JSONObject) json.get("keys");
		for (Object ks : keys.keySet()){
			String k = (String) ks;
			if (materialMatchesKey(k.charAt(0), mat)) return k.charAt(0);
		}
		return ' ';
	}
	
	public List<Material> getMaterialsOfKey(char key){
		List<Material> mats = new ArrayList<Material>();
		if (key==' '){ mats.add(Material.AIR); return mats;}
		JSONObject keys = (JSONObject) json.get("keys");
		JSONArray matA = (JSONArray) keys.get(String.valueOf(key));
		for (Object os : matA){
			mats.add(Material.valueOf(((String) os).split(":")[0]));
		}
		return mats;
	}
	
	public Position3 getRelativityFrom(Chest chest){
		Position3 rel = new Position3(0,1,0);
		switch(((org.bukkit.material.Chest) chest.getBlock()).getFacing()){
		case NORTH:
			rel.setX(-1);
			break;
		case SOUTH:
			rel.setX(1);
			break;
		case WEST:
			rel.setZ(-1);
			break;
		case EAST:
			rel.setZ(1);
			break;
		default:
			break;
		}
		return rel;
	}
	
	public boolean validAtChestLocation(Location loc){
		Block b = loc.getBlock();
		return b.getType()==Material.CHEST ? chestValid((Chest) b) : false;
	}
	
	public boolean chestValid(Chest chest){
		Position3 rel = getRelativityFrom(chest);
		Location origin = new Location(chest.getWorld(),
				chest.getX()-(chestLoc.getX()*-rel.getX()),
				chest.getY()-(chestLoc.getY()*-rel.getY()),
				chest.getZ()-(chestLoc.getZ()*-rel.getZ()));
		JSONArray Ys = (JSONArray) json.get("format");
		for (int y=0; y<(int) json.get("height"); y++){
			JSONArray Zs = (JSONArray) Ys.get(y);
			for (int z=0; z<(int) json.get("depth"); z++){
				String Xs = (String) Zs.get(z);
				for (int x=0; x<(int) json.get("width"); x++){
					Location current = new Location(origin.getWorld(),
							origin.getX()+(x*rel.getX()),
							origin.getY()+(y*rel.getY()),
							origin.getZ()+(z*rel.getZ()));
					if (!blockMatchesKey(Xs.charAt(x), current.getBlock())) return false;
				}
			}
		}
		return true;
	}
	
	public MechaFactoryRecipe getRecipeFromInput(Inventory input){
		JSONArray recipes = (JSONArray) json.get("recipes");
		for (Object o : recipes){
			String recipe = (String) o;
			if (Mechanization.recipes.containsKey(recipe) && Mechanization.recipes.get(recipe).inputMatches(input)) 
				return Mechanization.recipes.get(recipe);
		}
		return null;
	}
	
	public List<Position3> getFurnaceLocations(){
		List<Position3> furnaces = new ArrayList<Position3>();
		JSONArray Ys = (JSONArray) json.get("format");
		for (int y=0; y<(int) json.get("height"); y++){
			JSONArray Zs = (JSONArray) Ys.get(y);
			for (int z=0; z<(int) json.get("depth"); z++){
				String Xs = (String) Zs.get(z);
				for (int x=0; x<(int) json.get("width"); x++){
					if (getMaterialsOfKey(Xs.charAt(x)).contains(Material.FURNACE)){
						furnaces.add(new Position3(x,y,z));
					}
				}
			}
		}
		return furnaces;
	}
	
	public ItemStack getActivatorStack(){
		String a = (String) json.get("activator");
		if (a.equalsIgnoreCase("null")) return null;
		return a.startsWith("!") ?
				(Mechanization.items.containsKey(a.substring(1)) ? Mechanization.items.get(a.substring(1)).getItemStack() : null) : 
				new ItemStack(Material.valueOf(a));
	}
	
	public int getActivatorDamage(){
		return (int) json.get("damage_activator");
	}
	
	public int getActivatorConsumption(){
		return (int) json.get("consume_activator");
	}
	
	public void setInventoryToOutput(Inventory inv, MechaFactoryRecipe recipe){
		if (inv.getSize()!=27) return; // If not a chest.
		recipe.setInventoryToOutput(inv);
	}
}
