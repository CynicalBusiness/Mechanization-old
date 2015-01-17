package me.capit.mechanization.parser;

import org.bukkit.ChatColor;

import me.capit.eapi.data.Child;
import me.capit.eapi.data.DataModel;
import me.capit.mechanization.Mechanized;
import me.capit.mechanization.exception.MechaException;

public class MetaParser {
	
	private String name, description;
	
	public MetaParser(Mechanized mech) throws MechaException {
		Child child = mech.getModel().findFirstChild("meta");
		if (child==null || !(child instanceof DataModel)) throw new MechaException(mech, "Meta missing or invalid.");
		DataModel meta = (DataModel) child;
		
		name = meta.hasAttribute("display_name") ? meta.getAttribute("display_name").getValueString() : mech.getName();
		description = meta.hasAttribute("description") ? meta.getAttribute("description").getValueString() : "";
	}
	
	public String getName(){
		return ChatColor.translateAlternateColorCodes('&', name);
	}
	public String getRawName(){
		return name;
	}
	
	public String getDescription(){
		return ChatColor.translateAlternateColorCodes('&', description);
	}
	public String getRawDescription(){
		return description;
	}
	
}
