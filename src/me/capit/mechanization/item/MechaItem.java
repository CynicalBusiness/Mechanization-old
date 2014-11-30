package me.capit.mechanization.item;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import me.capit.mechanization.Mechanization;
import me.capit.mechanization.Mechanized;

public class MechaItem implements Mechanized, Serializable {
	private static final long serialVersionUID = 3159299245146020959L;
	private final String name;
	private JSONParser p = new JSONParser();
	private JSONObject json;
	
	public MechaItem(File file){
		name = file.getName().replaceFirst("[.][^.]+$", "");
		try {
			FileReader reader = new FileReader(file);
			json = (JSONObject) p.parse(reader);
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
		try {
			return (String) json.get("display_name");
		} catch (NullPointerException | ClassCastException e){
			Mechanization.logger.warning("Unable to fetch display name.");
			e.printStackTrace();
			return "";
		}
	}
	
	
	public ItemStack getItemStack(){return getItemStack(1);}
	public ItemStack getItemStack(int amount){
		try {
			ItemStack is = new ItemStack(Material.valueOf((String) json.get("root_item")), amount);
			is.setDurability((Short) json.get("root_item_dmg"));
			
			ItemMeta im = is.getItemMeta();
			JSONArray lore = (JSONArray) json.get("lore");
			List<String> loreA = new ArrayList<String>();
			for (Object s : lore){
				loreA.add((String) s);
			}
			im.setLore(loreA);
			im.setDisplayName((String) json.get("display_name"));
			
			is.setItemMeta(im);
			return is;
		} catch (NullPointerException | ClassCastException e){
			Mechanization.logger.warning("Unable to create item stack.");
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public JSONObject getJSON() {
		return json;
	}
}
