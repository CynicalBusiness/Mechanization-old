package me.capit.mechanization.factory;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.material.Directional;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import me.capit.mechanization.Mechanized;
import me.capit.mechanization.Position3;

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
	
	public boolean validAtLocation(Location loc){
		Block b = loc.getBlock();
		return b.getType()==Material.CHEST ? chestValid((Chest) b) : false;
	}
	
	public boolean chestValid(Chest chest){
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

}
