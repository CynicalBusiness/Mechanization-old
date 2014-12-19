package me.capit.mechanization;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.configuration.serialization.ConfigurationSerializable;

public class Position3 implements ConfigurationSerializable, Serializable {
	private static final long serialVersionUID = -2583242753363303725L;
	
	private double x,y,z;
	
	public static Position3 fromList(List<?> data){
		return new Position3((double) data.get(0), (double) data.get(1), (double) data.get(2));
	}
	
	public Position3(Map<String, Object> map){
		x = (double) map.get("X");
		y = (double) map.get("Y");
		z = (double) map.get("Z");
	}
	
	public Position3(double x, double y, double z){
		this.x=x; this.y=y; this.z=z;
	}
	
	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("X", x);
		map.put("Y", y);
		map.put("Z", z);
		return map;
	}
	
	public double getX(){
		return x;
	}
	
	public double getY(){
		return y;
	}
	
	public double getZ(){
		return z;
	}
	
	public void setX(double x){
		this.x=x;
	}
	
	public void setY(double y){
		this.y=y;
	}
	
	public void setZ(double z){
		this.z=z;
	}
	
	public Position3 inverse(){
		return new Position3(-x,-y,-z);
	}
	public Position3 reciprocal(){
		return new Position3(1/x,1/y,1/z);
	}
	
	public Position3 plus(Position3 pos){
		return new Position3(x+pos.getX(), y+pos.getY(), z+pos.getZ());
	}
	public Position3 minus(Position3 pos){
		return plus(inverse());
	}
	public Position3 times(Position3 pos){
		return new Position3(x*pos.getX(), y*pos.getY(), z*pos.getZ());
	}
	public Position3 divide(Position3 pos){
		return times(reciprocal());
	}

}
