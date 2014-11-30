package me.capit.mechanization.factory;

import java.util.HashMap;
import java.util.Map;

import me.capit.mechanization.Mechanization;
import me.capit.mechanization.Position3;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;

public class WorldFactory implements ConfigurationSerializable {
	{ConfigurationSerialization.registerClass(WorldFactory.class);}
	
	public final String factoryName; private Position3 origin; private World world;
	private boolean valid = false; private boolean running = false;
	
	public WorldFactory(Map<String, Object> data){
		factoryName = (String) data.get("FACTORY");
		origin = (Position3) data.get("POSITION");
		world = Bukkit.getWorld((String) data.get("WORLD"));
	}
	
	public void activate(){
		if (!valid) return;
		MechaFactory fac = Mechanization.factories.get(factoryName);
	}
	
	public void validate(){
		valid = Mechanization.factories.containsKey(factoryName) && 
				Mechanization.factories.get(factoryName).validAtLocation(new Location(world, origin.getX(), origin.getY(), origin.getZ()));
	}
	
	public boolean isValidated(){
		return valid;
	}
	
	public boolean isRunning(){
		return running;
	}
	
	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("FACTORY", factoryName);
		data.put("POSITION", origin);
		data.put("WORLD", world.getName());
		return data;
	}

}
