package me.capit.mechanization.factory;

import java.util.ArrayList;
import java.util.List;

import me.capit.mechanization.Position3;
import me.capit.mechanization.exception.MechaException;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jdom2.Element;

public class FactoryMatrix {
	private final Position3 dims,chestLoc;
	private final Element[][][] matrix;
	
	public FactoryMatrix(Element element) throws NullPointerException, IllegalArgumentException, MechaException{
		if (!element.getName().equals("matrix")) throw new MechaException().new InvalidElementException("matrix", element.getName());
		
		dims = new Position3(
				Integer.parseInt(element.getAttributeValue("width")),
				Integer.parseInt(element.getAttributeValue("height")),
				Integer.parseInt(element.getAttributeValue("depth")));
		chestLoc = new Position3(
				Integer.parseInt(element.getAttributeValue("chestX")),
				Integer.parseInt(element.getAttributeValue("chestY")),
				Integer.parseInt(element.getAttributeValue("chestZ")));
		
		try {
			matrix = new Element[(int) dims.getY()][(int) dims.getZ()][(int) dims.getX()];
			for (int j = 0; j<dims.getY(); j++) for (int k = 0; k<dims.getZ(); k++) for (int l = 0; l<dims.getX(); l++) 
				matrix[j][k][l] = element.getChildren().get(j).getChildren().get(k).getChildren().get(l);
			
			Element chest = matrix[(int) chestLoc.getY()][(int) chestLoc.getZ()][(int) chestLoc.getX()];
			if (!chest.getAttributeValue("material").equals("CHEST")) throw null;

			if (glitchInMatrix()) throw new ArrayIndexOutOfBoundsException();
		} catch (ArrayIndexOutOfBoundsException e){
			throw new MechaException("Format did not match dims.");
		}
	}
	
	private boolean glitchInMatrix(){ // Huehuehue.
		for (Element[][] e2 : matrix) for (Element[] e1 : e2) for (Element e : e1) if (e==null) return true;
		return false;
	}
	
	public boolean elementAtPositionRequriesData(Position3 pos){
		Element e = getElementAtPosition(pos);
		return e.getAttribute("data")!=null;
	}
	
	public Element getElementAtPosition(Position3 pos){
		return matrix[(int) pos.getY()][(int) pos.getZ()][(int) pos.getX()];
	}
	
	public Material getMaterialAtPosition(Position3 pos){
		Element ise = getElementAtPosition(pos);
		try {
			return Material.valueOf(ise.getAttributeValue("material"));
		} catch (NullPointerException e){
			return null;
		} catch (IllegalArgumentException e){
			e.printStackTrace();
			return Material.AIR;
		}
	}
	
	public short getDurabilityAtPosition(Position3 pos){
		Element ise = getElementAtPosition(pos);
		return ise.getAttribute("data")!=null ? Short.parseShort(ise.getAttributeValue("data")) : -1;
	}
	
	public ItemStack getItemStackAtPosition(Position3 pos){
		try {
			ItemStack is = new ItemStack(getMaterialAtPosition(pos));
			if (getDurabilityAtPosition(pos)>-1) is.setDurability(getDurabilityAtPosition(pos));
			return is;
		} catch (NullPointerException e){
			return null;
		}
	}
	
	public List<Position3> getLocationsOfMaterial(Material mat, int data){
		List<Position3> ps = new ArrayList<Position3>();
		for (int y = 0; y<dims.getY(); y++){ for (int z = 0; z<dims.getZ(); z++){ for (int x = 0; x<dims.getX(); x++){
			Position3 curpos = new Position3(x,y,z);
			if (getMaterialAtPosition(curpos)!=mat) continue;
			if (getDurabilityAtPosition(curpos)>-1 && data==getDurabilityAtPosition(curpos)) continue;
			ps.add(curpos);
		}}}
		return ps;
	}
	
	public Position3 getDims(){
		return dims;
	}
	
	public Position3 getChestLocation(){
		return chestLoc;
	}
}
